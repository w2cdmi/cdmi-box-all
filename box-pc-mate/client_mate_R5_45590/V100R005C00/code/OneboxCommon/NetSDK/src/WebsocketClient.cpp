#pragma warning(push)
#pragma warning(disable:4996)
#include <websocketpp/config/asio_client.hpp>
#include <websocketpp/client.hpp>
#include "WebsocketClient.h"
#include "RestClient.h"
#include "JsonParser.h"
#include "Utility.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("WebsocketClient")
#endif

#define PING_INTERVAL (45) // 45s

using websocketpp::lib::placeholders::_1;
using websocketpp::lib::placeholders::_2;
using websocketpp::lib::bind;

class WebsocketImpl
{
public:
	typedef WebsocketImpl type;
	typedef websocketpp::client<websocketpp::config::asio_tls_client> client_tls;
	typedef websocketpp::client<websocketpp::config::asio_client> client_no_tls;
	typedef websocketpp::config::asio_tls_client::message_type::ptr message_ptr;
	typedef websocketpp::lib::shared_ptr<boost::asio::ssl::context> context_ptr;
	typedef client_tls::connection_ptr connection_ptr;
	typedef websocketpp::lib::function<void(websocketpp::connection_hdl,message_ptr)> message_handler;

	WebsocketImpl():isSecure(true), isOpen_(true)
	{
	}

	int32_t init(const std::string& uri, const Configure& configure, message_handler h)
	{
		if (uri.empty())
		{
			SERVICE_ERROR(MODULE_NAME, RT_INVALID_PARAM, "the websocket uri is empty.");
			return RT_INVALID_PARAM;
		}

		SERVICE_DEBUG(MODULE_NAME, RT_OK, "the websocket uri is %s.", uri.c_str());

		configure_ = configure;

		websocketpp::lib::error_code ec;

		// register our handlers
		if (uri.find("wss:") != std::string::npos)
		{
			isSecure = true;

			endpoint_tls_.set_access_channels(websocketpp::log::alevel::all);
			endpoint_tls_.set_error_channels(websocketpp::log::elevel::all);

			// initialize asio
			endpoint_tls_.init_asio();

			endpoint_tls_.set_socket_init_handler(bind(&type::on_socket_init, this, ::_1));
			endpoint_tls_.set_tls_init_handler(bind(&type::on_tls_init, this, ::_1));
			endpoint_tls_.set_message_handler(h);
			endpoint_tls_.set_open_handler(bind(&type::on_open, this, ::_1));
			endpoint_tls_.set_close_handler(bind(&type::on_close, this, ::_1));
			endpoint_tls_.set_fail_handler(bind(&type::on_fail, this, ::_1));

			client_tls::connection_ptr con = endpoint_tls_.get_connection(uri, ec);
			if (ec)
			{
				SERVICE_ERROR(MODULE_NAME, ec.value(), 
					"websocket client failed to create the connection, uri: %s", 
					uri.c_str());
				return ec.value();
			}

			endpoint_tls_.connect(con);

			// start the asio io_service run loop
			endpoint_tls_.run();

			ec = con->get_ec();
		}
		else if (uri.find("ws:") != std::string::npos)
		{
			isSecure = false;

			endpoint_no_tls_.set_access_channels(websocketpp::log::alevel::all);
			endpoint_no_tls_.set_error_channels(websocketpp::log::elevel::all);

			// initialize asio
			endpoint_no_tls_.init_asio();

			endpoint_no_tls_.set_socket_init_handler(bind(&type::on_socket_init, this, ::_1));
			endpoint_no_tls_.set_message_handler(h);
			endpoint_no_tls_.set_open_handler(bind(&type::on_open, this, ::_1));
			endpoint_no_tls_.set_close_handler(bind(&type::on_close, this, ::_1));
			endpoint_no_tls_.set_fail_handler(bind(&type::on_fail, this, ::_1));

			client_no_tls::connection_ptr con = endpoint_no_tls_.get_connection(uri, ec);
			if (ec)
			{
				SERVICE_ERROR(MODULE_NAME, ec.value(), 
					"websocket client failed to create the connection, uri: %s", 
					uri.c_str());
				return ec.value();
			}

			endpoint_no_tls_.connect(con);

			// start the asio io_service run loop
			endpoint_no_tls_.run();

			ec = con->get_ec();
		}
		else
		{
			SERVICE_ERROR(MODULE_NAME, RT_INVALID_PARAM, 
				"the websocket uri: %s is invalid.", 
				uri.c_str());
			return RT_INVALID_PARAM;
		}

		if (ec)
		{
			SERVICE_ERROR(MODULE_NAME, ec.value(), 
				"request the websocket uri: %s failed, error code: %d, error message: %s.", 
				uri.c_str(), ec.value(), ec.message().c_str());
			return ec.value();
		}

		return RT_OK;
	}

	void on_socket_init(websocketpp::connection_hdl)
	{
		
	}

	context_ptr on_tls_init(websocketpp::connection_hdl)
	{
		context_ptr ctx = websocketpp::lib::make_shared<boost::asio::ssl::context>(boost::asio::ssl::context::tlsv12_client);
		try
		{
			ctx->set_options(boost::asio::ssl::context::default_workarounds |
				boost::asio::ssl::context::single_dh_use);
			ctx->set_verify_mode(boost::asio::ssl::context::verify_peer);
			ctx->load_verify_file(Utility::String::wstring_to_string(configure_.capath()));
		}
		catch (std::exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "%s", e.what());
		}
		return ctx;
	}

	void on_fail(websocketpp::connection_hdl hdl)
	{
		if (isSecure)
		{
			client_tls::connection_ptr con = endpoint_tls_.get_con_from_hdl(hdl);

			SERVICE_ERROR(MODULE_NAME, RT_ERROR, 
				"websocket failed, error code: %d, error message: %s", 
				con->get_ec().value(), con->get_ec().message().c_str());
		}
		else
		{
			client_no_tls::connection_ptr con = endpoint_no_tls_.get_con_from_hdl(hdl);

			SERVICE_ERROR(MODULE_NAME, RT_ERROR, 
				"websocket failed, error code: %d, error message: %s", 
				con->get_ec().value(), con->get_ec().message().c_str());
		}
	}

	void on_open(websocketpp::connection_hdl hdl)
	{
		isOpen_ = true;
		ping_thread_ = websocketpp::lib::thread(bind(&type::ping, this, hdl));
	}

	void on_close(websocketpp::connection_hdl)
	{
		isOpen_ = false;
		ping_thread_.interrupt();
		ping_thread_.join();
	}

	void ping(websocketpp::connection_hdl hdl)
	{
		try
		{
			while (isOpen_)
			{
				boost::this_thread::sleep(boost::posix_time::seconds(PING_INTERVAL));
				if (isSecure)
				{
					endpoint_tls_.ping(hdl, "");
				}
				else
				{
					endpoint_no_tls_.ping(hdl, "");
				}
				boost::this_thread::interruption_point();
			}
		}
		catch(boost::thread_interrupted& e)
		{
			UNUSED_ARG(e);
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "ping interrupt");
		}
		catch(std::exception& e)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "%s", e.what());
		}
	}

private:
	bool isSecure;
	client_tls endpoint_tls_;
	client_no_tls endpoint_no_tls_;
	websocketpp::lib::thread ping_thread_;
	bool isOpen_;
	Configure configure_;
};

class WebsocketClient::Impl
{
public:
	Impl(const TOKEN& token, const Configure& configure)
		:token_(token)
		,configure_(configure) {}

	int32_t getMsg(MsgInfoChangeHandler handler)
	{
		std::string websocketUrl = "";
		RestClient client(token_, configure_);
		int32_t ret = client.getMsgListener(websocketUrl);
		if (RT_OK != ret)
		{
			return ret;
		}
		if (websocketUrl.empty())
		{
			return RT_INVALID_PARAM;
		}

		WebsocketImpl websocketImlp;
		return websocketImlp.init(websocketUrl, configure_, bind(&WebsocketClient::Impl::on_getMsg_message, this, ::_1, ::_2, handler));
	}

	void on_getMsg_message(websocketpp::connection_hdl hdl, WebsocketImpl::message_ptr msg, MsgInfoChangeHandler handler)
	{
		try
		{
			MsgNode msgNode;
			DataBuffer data;
			data.pBuf = (unsigned char*)msg->get_payload().c_str();
			data.lBufLen = msg->get_payload().size();
			data.lOffset = data.lBufLen;
			if (RT_OK != JsonParser::parseMsgInfo(data, msgNode))
			{
				SERVICE_DEBUG(MODULE_NAME, RT_OK, 
					"failed to parse message json: %s", 
					msg->get_payload().c_str());
				return;
			}
			handler(msgNode);
		}
		catch(boost::thread_interrupted)
		{
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "the websocket is interrupted.");
		}
	}

private:
	TOKEN token_;
	Configure configure_;
};

WebsocketClient::WebsocketClient(const TOKEN& token, const Configure& configure)
{
	try
	{
		impl.reset(new Impl(token, configure));
	}
	catch(...) {}
}

int32_t WebsocketClient::getMsg(MsgInfoChangeHandler handler)
{
	return impl->getMsg(handler);
}
#pragma warning(pop)
