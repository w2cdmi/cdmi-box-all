/*! WebUploader 0.1.5 */ ;
var dragEnter = dragOver = dragLeave = dragDrop = function() {};
(function(f, d) {
	var c = {},
		b = function(n, o) {
			var l, k, m;
			if(typeof n === "string") {
				return e(n)
			} else {
				l = [];
				for(k = n.length, m = 0; m < k; m++) {
					l.push(e(n[m]))
				}
				return o.apply(null, l)
			}
		},
		j = function(m, l, k) {
			if(arguments.length === 2) {
				k = l;
				l = null
			}
			b(l || [], function() {
				i(m, k, arguments)
			})
		},
		i = function(o, k, l) {
			var m = {
					exports: k
				},
				n;
			if(typeof k === "function") {
				l.length || (l = [b, m.exports, m]);
				n = k.apply(null, l);
				n !== undefined && (m.exports = n)
			}
			c[o] = m.exports
		},
		e = function(l) {
			var k = c[l] || f[l];
			if(!k) {
				throw new Error("`" + l + "` is undefined")
			}
			return k
		},
		h = function(q) {
			var l, o, p, k, n, m;
			m = function(r) {
				return r && (r.charAt(0).toUpperCase() + r.substr(1))
			};
			for(l in c) {
				o = q;
				if(!c.hasOwnProperty(l)) {
					continue
				}
				p = l.split("/");
				n = m(p.pop());
				while((k = m(p.shift()))) {
					o[k] = o[k] || {};
					o = o[k]
				}
				o[n] = c[l]
			}
			return q
		},
		a = function(k) {
			f.__dollar = k;
			return h(d(f, j, b))
		},
		g;
	if(typeof module === "object" && typeof module.exports === "object") {
		module.exports = a()
	} else {
		if(typeof define === "function" && define.amd) {
			define(["jQuery"], a)
		} else {
			g = f.WebUploader;
			f.WebUploader = a();
			f.WebUploader.noConflict = function() {
				f.WebUploader = g
			}
		}
	}
})(window, function(b, c, a) {
	c("dollar-third", [], function() {
		var d = b.__dollar || b.jQuery || b.Zepto;
		if(!d) {
			throw new Error("jQuery or Zepto not found!")
		}
		return d
	});
	c("dollar", ["dollar-third"], function(d) {
		return d
	});
	c("promise-third", ["dollar"], function(d) {
		return {
			Deferred: d.Deferred,
			when: d.when,
			isPromise: function(e) {
				return e && typeof e.then === "function"
			}
		}
	});
	c("promise", ["promise-third"], function(d) {
		return d
	});
	c("base", ["dollar", "promise"], function(h, j) {
		var g = function() {},
			f = Function.call;

		function e(k) {
			return function() {
				return f.apply(k, arguments)
			}
		}

		function i(l, k) {
			return function() {
				return l.apply(k, arguments)
			}
		}

		function d(k) {
			var l;
			if(Object.create) {
				return Object.create(k)
			} else {
				l = function() {};
				l.prototype = k;
				return new l()
			}
		}
		return {
			version: "0.1.5",
			$: h,
			Deferred: j.Deferred,
			isPromise: j.isPromise,
			when: j.when,
			browser: (function(o) {
				var n = {},
					m = o.match(/WebKit\/([\d.]+)/),
					k = o.match(/Chrome\/([\d.]+)/) || o.match(/CriOS\/([\d.]+)/),
					r = o.match(/MSIE\s([\d\.]+)/) || o.match(/(?:trident)(?:.*rv:([\w.]+))?/i),
					p = o.match(/Firefox\/([\d.]+)/),
					q = o.match(/Safari\/([\d.]+)/),
					l = o.match(/OPR\/([\d.]+)/);
				m && (n.webkit = parseFloat(m[1]));
				k && (n.chrome = parseFloat(k[1]));
				r && (n.ie = parseFloat(r[1]));
				p && (n.firefox = parseFloat(p[1]));
				q && (n.safari = parseFloat(q[1]));
				l && (n.opera = parseFloat(l[1]));
				return n
			})(navigator.userAgent),
			os: (function(m) {
				var l = {},
					k = m.match(/(?:Android);?[\s\/]+([\d.]+)?/),
					n = m.match(/(?:iPad|iPod|iPhone).*OS\s([\d_]+)/);
				k && (l.android = parseFloat(k[1]));
				n && (l.ios = parseFloat(n[1].replace(/_/g, ".")));
				return l
			})(navigator.userAgent),
			inherits: function(m, l, k) {
				var n;
				if(typeof l === "function") {
					n = l;
					l = null
				} else {
					if(l && l.hasOwnProperty("constructor")) {
						n = l.constructor
					} else {
						n = function() {
							return m.apply(this, arguments)
						}
					}
				}
				h.extend(true, n, m, k || {});
				n.__super__ = m.prototype;
				n.prototype = d(m.prototype);
				l && h.extend(true, n.prototype, l);
				return n
			},
			noop: g,
			bindFn: i,
			log: (function() {
				if(b.console) {
					return i(console.log, console)
				}
				return g
			})(),
			nextTick: (function() {
				return function(k) {
					setTimeout(k, 1)
				}
			})(),
			slice: e([].slice),
			guid: (function() {
				var k = 0;
				return function(n) {
					var l = (+new Date()).toString(32),
						m = 0;
					for(; m < 5; m++) {
						l += Math.floor(Math.random() * 65535).toString(32)
					}
					return(n || "wu_") + l + (k++).toString(32)
				}
			})(),
			formatSize: function(m, k, l) {
				var n;
				l = l || ["B", "K", "M", "G", "TB"];
				while((n = l.shift()) && m > 1024) {
					m = m / 1024
				}
				return(n === "B" ? m : m.toFixed(k || 2)) + n
			}
		}
	});
	c("mediator", ["base"], function(f) {
		var g = f.$,
			k = [].slice,
			j = /\s+/,
			e;

		function d(l, m, o, n) {
			return g.grep(l, function(p) {
				return p && (!m || p.e === m) && (!o || p.cb === o || p.cb._cb === o) && (!n || p.ctx === n)
			})
		}

		function i(l, n, m) {
			g.each((l || "").split(j), function(o, p) {
				m(p, n)
			})
		}

		function h(p, n) {
			var m = false,
				o = -1,
				l = p.length,
				q;
			while(++o < l) {
				q = p[o];
				if(q.cb.apply(q.ctx2, n) === false) {
					m = true;
					break
				}
			}
			return !m
		}
		e = {
			on: function(l, p, m) {
				var n = this,
					o;
				if(!p) {
					return this
				}
				o = this._events || (this._events = []);
				i(l, p, function(q, s) {
					var r = {
						e: q
					};
					r.cb = s;
					r.ctx = m;
					r.ctx2 = m || n;
					r.id = o.length;
					o.push(r)
				});
				return this
			},
			once: function(l, o, m) {
				var n = this;
				if(!o) {
					return n
				}
				i(l, o, function(p, r) {
					var q = function() {
						n.off(p, q);
						return r.apply(m || n, arguments)
					};
					q._cb = r;
					n.on(p, q, m)
				});
				return n
			},
			off: function(n, l, m) {
				var o = this._events;
				if(!o) {
					return this
				}
				if(!n && !l && !m) {
					this._events = [];
					return this
				}
				i(n, l, function(q, p) {
					g.each(d(o, q, p, m), function() {
						delete o[this.id]
					})
				});
				return this
			},
			trigger: function(o) {
				var m, n, l;
				if(!this._events || !o) {
					return this
				}
				m = k.call(arguments, 1);
				n = d(this._events, o);
				l = d(this._events, "all");
				return h(n, m) && h(l, arguments)
			}
		};
		return g.extend({
			installTo: function(l) {
				return g.extend(l, e)
			}
		}, e)
	});
	c("uploader", ["base", "mediator"], function(d, f) {
		var e = d.$;

		function g(h) {
			this.options = e.extend(true, {}, g.options, h);
            this._init(this.options)
		}
		g.options = {};
		f.installTo(g.prototype);
		e.each({
			upload: "start-upload",
			stop: "stop-upload",
			getFile: "get-file",
			getFiles: "get-files",
			addFile: "add-file",
			addFiles: "add-file",
			sort: "sort-files",
			removeFile: "remove-file",
			skipFile: "skip-file",
			retry: "retry",
			isInProgress: "is-in-progress",
			makeThumb: "make-thumb",
			md5File: "md5-file",
			getDimension: "get-dimension",
			addButton: "add-btn",
			getRuntimeType: "get-runtime-type",
			refresh: "refresh",
			disable: "disable",
			enable: "enable",
			reset: "reset"
		}, function(h, i) {
			g.prototype[h] = function() {
				return this.request(i, arguments)
			}
		});
		e.extend(g.prototype, {
			state: "pending",
			_init: function(i) {
				var h = this;
				h.request("init", i, function() {
					h.state = "ready";
					h.trigger("ready")
				})
			},
			option: function(h, j) {
				var i = this.options;
				if(arguments.length > 1) {
					if(e.isPlainObject(j) && e.isPlainObject(i[h])) {
						e.extend(i[h], j)
					} else {
						i[h] = j
					}
				} else {
					return h ? i[h] : i
				}
			},
			getStats: function() {
				var h = this.request("get-stats");
				return {
					successNum: h.numOfSuccess,
					progressNum: h.numOfProgress,
					cancelNum: h.numOfCancel,
					invalidNum: h.numOfInvalid,
					uploadFailNum: h.numOfUploadFailed,
					queueNum: h.numOfQueue
				}
			},
			trigger: function(j) {
				var i = [].slice.call(arguments, 1),
					k = this.options,
					h = "on" + j.substring(0, 1).toUpperCase() + j.substring(1);
				if(f.trigger.apply(this, arguments) === false || e.isFunction(k[h]) && k[h].apply(this, i) === false || e.isFunction(this[h]) && this[h].apply(this, i) === false || f.trigger.apply(f, [this, j].concat(i)) === false) {
					return false
				}
				return true
			},
			request: d.noop
		});
		d.create = g.create = function(h) {
			return new g(h)
		};
		d.Uploader = g;
		return g
	});
	c("runtime/runtime", ["base", "mediator"], function(e, h) {
		var g = e.$,
			f = {},
			i = function(k) {
				for(var j in k) {
					if(k.hasOwnProperty(j)) {
						return j
					}
				}
				return null
			};

		function d(j) {
			this.options = g.extend({
				container: document.body
			}, j);
			this.uid = e.guid("rt_")
		}
		g.extend(d.prototype, {
			getContainer: function() {
				var l = this.options,
					k, j;
				if(this._container) {
					return this._container
				}
				k = g(l.container || document.body);
				j = g(document.createElement("div"));
				j.attr("id", "rt_" + this.uid);
				j.css({
					position: "absolute",
					top: "0px",
					left: "0px",
					width: "1px",
					height: "1px",
					overflow: "hidden"
				});
				k.append(j);
				k.addClass("webuploader-container");
				this._container = j;
				return j
			},
			init: e.noop,
			exec: e.noop,
			destroy: function() {
				if(this._container) {
					this._container.parentNode.removeChild(this.__container)
				}
				this.off()
			}
		});
		d.orders = "html5,flash";
		d.addRuntime = function(k, j) {
			f[k] = j
		};
		d.hasRuntime = function(j) {
			return !!(j ? f[j] : i(f))
		};
		d.create = function(m, k) {
			var j, l;
			k = k || d.orders;
			g.each(k.split(/\s*,\s*/g), function() {
				if(f[this]) {
					j = this;
					return false
				}
			});
			j = j || i(f);
			if(!j) {
				throw new Error("Runtime Error")
			}
			l = new f[j](m);
			return l
		};
		h.installTo(d.prototype);
		return d
	});
	c("runtime/client", ["base", "mediator", "runtime/runtime"], function(g, h, f) {
		var d;
		d = (function() {
			var i = {};
			return {
				add: function(j) {
					i[j.uid] = j
				},
				get: function(k, j) {
					var l;
					if(k) {
						return i[k]
					}
					for(l in i) {
						if(j && i[l].__standalone) {
							continue
						}
						return i[l]
					}
					return null
				},
				remove: function(j) {
					delete i[j.uid]
				}
			}
		})();

		function e(k, j) {
			var i = g.Deferred(),
				l;
			this.uid = g.guid("client_");
			this.runtimeReady = function(m) {
				return i.done(m)
			};
			this.connectRuntime = function(n, m) {
				if(l) {
					throw new Error("already connected!")
				}
				i.done(m);
				if(typeof n === "string" && d.get(n)) {
					l = d.get(n)
				}
				l = l || d.get(null, j);
				if(!l) {
					l = f.create(n, n.runtimeOrder);
					l.__promise = i.promise();
					l.once("ready", i.resolve);
					l.init();
					d.add(l);
					l.__client = 1
				} else {
					g.$.extend(l.options, n);
					l.__promise.then(i.resolve);
					l.__client++
				}
				j && (l.__standalone = j);
				return l
			};
			this.getRuntime = function() {
				return l
			};
			this.disconnectRuntime = function() {
				if(!l) {
					return
				}
				l.__client--;
				if(l.__client <= 0) {
					d.remove(l);
					delete l.__promise;
					l.destroy()
				}
				l = null
			};
			this.exec = function() {
				if(!l) {
					return
				}
				var m = g.slice(arguments);
				k && m.unshift(k);
				return l.exec.apply(this, m)
			};
			this.getRuid = function() {
				return l && l.uid
			};
			this.destroy = (function(m) {
				return function() {
					m && m.apply(this, arguments);
					this.trigger("destroy");
					this.off();
					this.exec("destroy");
					this.disconnectRuntime()
				}
			})(this.destroy)
		}
		h.installTo(e.prototype);
		return e
	});
	c("lib/dnd", ["base", "mediator", "runtime/client"], function(e, h, d) {
		var f = e.$;

		function g(i) {
			i = this.options = f.extend({}, g.options, i);
			i.container = f(i.container);
			if(!i.container.length) {
				return
			}
			d.call(this, "DragAndDrop")
		}
		g.options = {
			accept: null,
			disableGlobalDnd: false
		};
		e.inherits(d, {
			constructor: g,
			init: function() {
				var i = this;
				i.connectRuntime(i.options, function() {
					i.exec("init");
					i.trigger("ready")
				})
			},
			destroy: function() {
				this.disconnectRuntime()
			}
		});
		h.installTo(g.prototype);
		return g
	});
	c("widgets/widget", ["base", "uploader"], function(e, k) {
		var h = e.$,
			g = k.prototype._init,
			j = {},
			i = [];

		function d(n) {
			if(!n) {
				return false
			}
			var m = n.length,
				l = h.type(n);
			if(n.nodeType === 1 && m) {
				return true
			}
			return l === "array" || l !== "function" && l !== "string" && (m === 0 || typeof m === "number" && m > 0 && (m - 1) in n)
		}

		function f(l) {
			this.owner = l;
			this.options = l.options
		}
		h.extend(f.prototype, {
			init: e.noop,
			invoke: function(l, m) {
				var n = this.responseMap;
				if(!n || !(l in n) || !(n[l] in this) || !h.isFunction(this[n[l]])) {
					return j
				}
				return this[n[l]].apply(this, m)
			},
			request: function() {
				return this.owner.request.apply(this.owner, arguments)
			}
		});
		h.extend(k.prototype, {
			_init: function() {
				var m = this,
					l = m._widgets = [];
				h.each(i, function(o, n) {
					l.push(new n(m))
				});
				return g.apply(m, arguments)
			},
			request: function(m, s, v) {
				var p = 0,
					t = this._widgets,
					r = t.length,
					o = [],
					n = [],
					q, l, w, u;
				s = d(s) ? s : [s];
				for(; p < r; p++) {
					q = t[p];
					l = q.invoke(m, s);
					if(l !== j) {
						if(e.isPromise(l)) {
							n.push(l)
						} else {
							o.push(l)
						}
					}
				}
				if(v || n.length) {
					w = e.when.apply(e, n);
					u = w.pipe ? "pipe" : "then";
					return w[u](function() {
						var x = e.Deferred(),
							y = arguments;
						if(y.length === 1) {
							y = y[0]
						}
						setTimeout(function() {
							x.resolve(y)
						}, 1);
						return x.promise()
					})[v ? u : "done"](v || e.noop)
				} else {
					return o[0]
				}
			}
		});
		k.register = f.register = function(m, o) {
			var n = {
					init: "init"
				},
				l;
			if(arguments.length === 1) {
				o = m;
				o.responseMap = n
			} else {
				o.responseMap = h.extend(n, m)
			}
			l = e.inherits(f, o);
			i.push(l);
			return l
		};
		return f
	});
	c("widgets/filednd", ["base", "uploader", "lib/dnd", "widgets/widget"], function(e, g, d) {
		var f = e.$;
		g.options.dnd = "";
		return g.register({
			init: function(l) {
				if(!l.dnd || this.request("predict-runtime-type") !== "html5") {
					return
				}
				var k = this,
					h = e.Deferred(),
					i = f.extend({}, {
						disableGlobalDnd: l.disableGlobalDnd,
						container: l.dnd,
						accept: l.accept
					}),
					j;
				j = new d(i);
				j.once("ready", h.resolve);
				j.on("drop", function(m) {
					k.request("add-file", [m])
				});
				j.on("accept", function(m) {
					return k.owner.trigger("dndAccept", m)
				});
				j.init();
				return h.promise()
			}
		})
	});
	c("lib/filepaste", ["base", "mediator", "runtime/client"], function(f, h, e) {
		var g = f.$;

		function d(i) {
			i = this.options = g.extend({}, i);
			i.container = g(i.container || document.body);
			e.call(this, "FilePaste")
		}
		f.inherits(e, {
			constructor: d,
			init: function() {
				var i = this;
				i.connectRuntime(i.options, function() {
					i.exec("init");
					i.trigger("ready")
				})
			},
			destroy: function() {
				this.exec("destroy");
				this.disconnectRuntime();
				this.off()
			}
		});
		h.installTo(d.prototype);
		return d
	});
	c("widgets/filepaste", ["base", "uploader", "lib/filepaste", "widgets/widget"], function(e, g, d) {
		var f = e.$;
		return g.register({
			init: function(k) {
				if(!k.paste || this.request("predict-runtime-type") !== "html5") {
					return
				}
				var j = this,
					h = e.Deferred(),
					i = f.extend({}, {
						container: k.paste,
						accept: k.accept
					}),
					l;
				l = new d(i);
				l.once("ready", h.resolve);
				l.on("paste", function(m) {
					j.owner.request("add-file", [m])
				});
				l.init();
				return h.promise()
			}
		})
	});
	c("lib/blob", ["base", "runtime/client"], function(e, d) {
		function f(g, i) {
			var h = this;
			h.source = i;
			h.ruid = g;
			this.size = i.size || 0;
			if(!i.type && ~"jpg,jpeg,png,gif,bmp".indexOf(this.ext)) {
				this.type = "image/" + (this.ext === "jpg" ? "jpeg" : this.ext)
			} else {
				this.type = i.type || "application/octet-stream"
			}
			d.call(h, "Blob");
			this.uid = i.uid || this.uid;
			if(g) {
				h.connectRuntime(g)
			}
		}
		e.inherits(d, {
			constructor: f,
			slice: function(h, g) {
				return this.exec("slice", h, g)
			},
			getSource: function() {
				return this.source
			}
		});
		return f
	});
	c("lib/file", ["base", "lib/blob"], function(g, h) {
		var f = 1,
			d = /\.([^.]+)$/;

		function e(i, j) {
			var k;
			this.name = j.name || ("untitled" + f++);
			k = d.exec(j.name) ? RegExp.$1.toLowerCase() : "";
			if(!k && j.type) {
				k = /\/(jpg|jpeg|png|gif|bmp)$/i.exec(j.type) ? RegExp.$1.toLowerCase() : "";
				this.name += "." + k
			}
			this.ext = k;
			this.lastModifiedDate = j.lastModifiedDate || (new Date()).toLocaleString();
			h.apply(this, arguments)
		}
		return g.inherits(h, e)
	});
	c("lib/filepicker", ["base", "runtime/client", "lib/file"], function(g, f, e) {
		var h = g.$;

		function d(i) {
			i = this.options = h.extend({}, d.options, i);
			i.container = h(i.id);
			if(!i.container.length) {
				throw new Error("按钮指定错误")
			}
			i.innerHTML = i.innerHTML || i.label || i.container.html() || "";
			i.button = h(i.button || document.createElement("div"));
			i.button.html(i.innerHTML);
			i.container.html(i.button);
			f.call(this, "FilePicker", true)
		}
		d.options = {
			button: null,
			container: null,
			label: null,
			innerHTML: null,
			multiple: true,
			accept: null,
			name: "file"
		};
		g.inherits(f, {
			constructor: d,
			init: function() {
				var k = this,
					j = k.options,
					i = j.button;
				i.addClass("webuploader-pick");
				k.on("all", function(l) {
					var m;
					switch(l) {
						case "mouseenter":
							i.addClass("webuploader-pick-hover");
							break;
						case "mouseleave":
							i.removeClass("webuploader-pick-hover");
							break;
						case "change":
							m = k.exec("getFiles");
							k.trigger("select", h.map(m, function(n) {
								n = new e(k.getRuid(), n);
								n._refer = j.container;
								return n
							}), j.container);
							break
					}
				});
				k.connectRuntime(j, function() {
					k.refresh();
					k.exec("init", j);
					k.trigger("ready")
				});
				h(b).on("resize", function() {
					k.refresh()
				})
			},
			refresh: function() {
				var j = this.getRuntime().getContainer(),
					k = this.options.button,
					l = k.outerWidth ? k.outerWidth() : k.width(),
					i = k.outerHeight ? k.outerHeight() : k.height(),
					m = k.offset();
				l && i && j.css({
					bottom: "auto",
					right: "auto",
					width: l + "px",
					height: i + "px"
				}).offset(m)
			},
			enable: function() {
				var i = this.options.button;
				i.removeClass("webuploader-pick-disable");
				this.refresh()
			},
			disable: function() {
				var i = this.options.button;
				this.getRuntime().getContainer().css({
					top: "-99999px"
				});
				i.addClass("webuploader-pick-disable")
			},
			destroy: function() {
				if(this.runtime) {
					this.exec("destroy");
					this.disconnectRuntime()
				}
			}
		});
		return d
	});
	c("widgets/filepicker", ["base", "uploader", "lib/filepicker", "widgets/widget"], function(e, g, d) {
		var f = e.$;
		f.extend(g.options, {
			pick: null,
			accept: null
		});
		return g.register({
			"add-btn": "addButton",
			refresh: "refresh",
			disable: "disable",
			enable: "enable"
		}, {
			init: function(h) {
				this.pickers = [];
				return h.pick && this.addButton(h.pick)
			},
			refresh: function() {
				f.each(this.pickers, function() {
					this.refresh()
				})
			},
			addButton: function(j) {
				var l = this,
					k = l.options,
					i = k.accept,
					h = [];
				if(!j) {
					return
				}
				f.isPlainObject(j) || (j = {
					id: j
				});
				f(j.id).each(function() {
					var o, n, m;
					m = e.Deferred();
					o = f.extend({}, j, {
						accept: f.isPlainObject(i) ? [i] : i,
						swf: k.swf,
						runtimeOrder: k.runtimeOrder,
						id: this
					});
					n = new d(o);
					n.once("ready", m.resolve);
					n.on("select", function(p) {
						l.owner.request("add-file", [p])
					});
					n.init();
					l.pickers.push(n);
					h.push(m.promise())
				});
				return e.when.apply(e, h)
			},
			disable: function() {
				f.each(this.pickers, function() {
					this.disable()
				})
			},
			enable: function() {
				f.each(this.pickers, function() {
					this.enable()
				})
			}
		})
	});
	c("lib/image", ["base", "runtime/client", "lib/blob"], function(f, e, h) {
		var g = f.$;

		function d(i) {
			this.options = g.extend({}, d.options, i);
			e.call(this, "Image");
			this.on("load", function() {
				this._info = this.exec("info");
				this._meta = this.exec("meta")
			})
		}
		d.options = {
			quality: 90,
			crop: false,
			preserveHeaders: false,
			allowMagnify: false
		};
		f.inherits(e, {
			constructor: d,
			info: function(i) {
				if(i) {
					this._info = i;
					return this
				}
				return this._info
			},
			meta: function(i) {
				if(i) {
					this._meta = i;
					return this
				}
				return this._meta
			},
			loadFromBlob: function(i) {
				var k = this,
					j = i.getRuid();
				this.connectRuntime(j, function() {
					k.exec("init", k.options);
					k.exec("loadFromBlob", i)
				})
			},
			resize: function() {
				var i = f.slice(arguments);
				return this.exec.apply(this, ["resize"].concat(i))
			},
			crop: function() {
				var i = f.slice(arguments);
				return this.exec.apply(this, ["crop"].concat(i))
			},
			getAsDataUrl: function(i) {
				return this.exec("getAsDataUrl", i)
			},
			getAsBlob: function(j) {
				var i = this.exec("getAsBlob", j);
				return new h(this.getRuid(), i)
			}
		});
		return d
	});
	c("widgets/image", ["base", "uploader", "lib/image", "widgets/widget"], function(e, h, d) {
		var g = e.$,
			f;
		f = (function(i) {
			var j = 0,
				l = [],
				k = function() {
					var m;
					while(l.length && j < i) {
						m = l.shift();
						j += m[0];
						m[1]()
					}
				};
			return function(o, n, m) {
				l.push([n, m]);
				o.once("destroy", function() {
					j -= n;
					setTimeout(k, 1)
				});
				setTimeout(k, 1)
			}
		})(5 * 1024 * 1024);
		g.extend(h.options, {
			thumb: {
				width: 110,
				height: 110,
				quality: 70,
				allowMagnify: true,
				crop: true,
				preserveHeaders: false,
				type: "image/jpeg"
			},
			compress: {
				width: 1600,
				height: 1600,
				quality: 90,
				allowMagnify: false,
				crop: false,
				preserveHeaders: true
			}
		});
		return h.register({
			"make-thumb": "makeThumb",
			"before-send-file": "compressImage"
		}, {
			makeThumb: function(k, j, l, i) {
				var m, n;
				k = this.request("get-file", k);
				if(!k.type.match(/^image/)) {
					j(true);
					return
				}
				m = g.extend({}, this.options.thumb);
				if(g.isPlainObject(l)) {
					m = g.extend(m, l);
					l = null
				}
				l = l || m.width;
				i = i || m.height;
				n = new d(m);
				n.once("load", function() {
					k._info = k._info || n.info();
					k._meta = k._meta || n.meta();
					if(l <= 1 && l > 0) {
						l = k._info.width * l
					}
					if(i <= 1 && i > 0) {
						i = k._info.height * i
					}
					n.resize(l, i)
				});
				n.once("complete", function() {
					j(false, n.getAsDataUrl(m.type));
					n.destroy()
				});
				n.once("error", function(o) {
					j(o || true);
					n.destroy()
				});
				f(n, k.source.size, function() {
					k._info && n.info(k._info);
					k._meta && n.meta(k._meta);
					n.loadFromBlob(k.source)
				})
			},
			compressImage: function(k) {
				var m = this.options.compress || this.options.resize,
					j = m && m.compressSize || 0,
					l = m && m.noCompressIfLarger || false,
					n, i;
				k = this.request("get-file", k);
				if(!m || !~"image/jpeg,image/jpg".indexOf(k.type) || k.size < j || k._compressed) {
					return
				}
				m = g.extend({}, m);
				i = e.Deferred();
				n = new d(m);
				i.always(function() {
					n.destroy();
					n = null
				});
				n.once("error", i.reject);
				n.once("load", function() {
					var p = m.width,
						o = m.height;
					k._info = k._info || n.info();
					k._meta = k._meta || n.meta();
					if(p <= 1 && p > 0) {
						p = k._info.width * p
					}
					if(o <= 1 && o > 0) {
						o = k._info.height * o
					}
					n.resize(p, o)
				});
				n.once("complete", function() {
					var o, p;
					try {
						o = n.getAsBlob(m.type);
						p = k.size;
						if(!l || o.size < p) {
							k.source = o;
							k.size = o.size;
							k.trigger("resize", o.size, p)
						}
						k._compressed = true;
						i.resolve()
					} catch(q) {
						i.resolve()
					}
				});
				k._info && n.info(k._info);
				k._meta && n.meta(k._meta);
				n.loadFromBlob(k.source);
				return i.promise()
			}
		})
	});
	c("file", ["base", "mediator"], function(h, k) {
		var f = h.$,
			d = "WU_FILE_",
			g = 0,
			i = /\.([^.]+)$/,
			e = {};

		function j() {
			return d + g++
		}

		function l(m) {
			this.name = m.name || "Untitled";
			this.size = m.size || 0;
			this.type = m.type || "application";
			this.lastModifiedDate = m.lastModifiedDate || (new Date() * 1);
			this.id = j();
			this.ext = i.exec(this.name) ? RegExp.$1 : "";
			this.statusText = "";
			e[this.id] = l.Status.INITED;
			this.source = m;
			this.loaded = 0;
			this.on("error", function(n) {
				this.setStatus(l.Status.ERROR, n)
			})
		}
		f.extend(l.prototype, {
			setStatus: function(n, o) {
				var m = e[this.id];
				typeof o !== "undefined" && (this.statusText = o);
				if(n !== m) {
					e[this.id] = n;
					this.trigger("statuschange", n, m)
				}
			},
			getStatus: function() {
				return e[this.id]
			},
			getSource: function() {
				return this.source
			},
			destory: function() {
				delete e[this.id]
			}
		});
		k.installTo(l.prototype);
		l.Status = {
			INITED: "inited",
			QUEUED: "queued",
			PROGRESS: "progress",
			ERROR: "error",
			COMPLETE: "complete",
			CANCELLED: "cancelled",
			INTERRUPT: "interrupt",
			INVALID: "invalid"
		};
		return l
	});
	c("queue", ["base", "mediator", "file"], function(e, i, h) {
		var g = e.$,
			d = h.Status;
 
		function f() {
			this.stats = {
				numOfQueue: 0,
				numOfSuccess: 0,
				numOfCancel: 0,
				numOfProgress: 0,
				numOfUploadFailed: 0,
				numOfInvalid: 0
			};
			this._queue = [];
			this._map = {}
		}
		g.extend(f.prototype, {
			append: function(j) {
				this._queue.push(j);
				this._fileAdded(j);
				return this
			},
			prepend: function(j) {
				this._queue.unshift(j);
				this._fileAdded(j);
				return this
			},
			getFile: function(j) {
				if(typeof j !== "string") {
					return j
				}
				return this._map[j]
			},
			fetch: function(k) {
				var j = this._queue.length,
					m, l;
				k = k || d.QUEUED;
				for(m = 0; m < j; m++) {
					l = this._queue[m];
					if(k === l.getStatus()) {
						return l
					}
				}
				return null
			},
			sort: function(j) {
				if(typeof j === "function") {
					this._queue.sort(j)
				}
			},
			getFiles: function() {
				var n = [].slice.call(arguments, 0),
					k = [],
					m = 0,
					j = this._queue.length,
					l;
				for(; m < j; m++) {
					l = this._queue[m];
					if(n.length && !~g.inArray(l.getStatus(), n)) {
						continue
					}
					k.push(l)
				}
				return k
			},
			_fileAdded: function(j) {
				var l = this,
					k = this._map[j.id];
				if(!k) {
					this._map[j.id] = j;
					j.on("statuschange", function(n, m) {
						l._onFileStatusChange(n, m)
					})
				}
				j.setStatus(d.QUEUED)
			},
			_onFileStatusChange: function(j, k) {
				var l = this.stats;
				switch(k) {
					case d.PROGRESS:
						l.numOfProgress--;
						break;
					case d.QUEUED:
						l.numOfQueue--;
						break;
					case d.ERROR:
						l.numOfUploadFailed--;
						break;
					case d.INVALID:
						l.numOfInvalid--;
						break
				}
				switch(j) {
					case d.QUEUED:
						l.numOfQueue++;
						break;
					case d.PROGRESS:
						l.numOfProgress++;
						break;
					case d.ERROR:
						l.numOfUploadFailed++;
						break;
					case d.COMPLETE:
						l.numOfSuccess++;
						break;
					case d.CANCELLED:
						l.numOfCancel++;
						break;
					case d.INVALID:
						l.numOfInvalid++;
						break
				}
			}
		});
		i.installTo(f.prototype);
		return f
	});
	c("widgets/queue", ["base", "uploader", "queue", "file", "lib/file", "runtime/client", "widgets/widget"], function(g, d, e, l, i, k) {
		var f = g.$,
			h = /\.\w+$/,
			j = l.Status;
		return d.register({
			"sort-files": "sortFiles",
			"add-file": "addFiles",
			"get-file": "getFile",
			"fetch-file": "fetchFile",
			"get-stats": "getStats",
			"get-files": "getFiles",
			"remove-file": "removeFile",
			retry: "retry",
			reset: "reset",
			"accept-file": "acceptFile"
		}, {
			init: function(m) {
				var s = this,
					u, r, p, t, q, n, o;
				if(f.isPlainObject(m.accept)) {
					m.accept = [m.accept]
				}
				if(m.accept) {
					q = [];
					for(p = 0, r = m.accept.length; p < r; p++) {
						t = m.accept[p].extensions;
						t && q.push(t)
					}
					if(q.length) {
						n = "\\." + q.join(",").replace(/,/g, "$|\\.").replace(/\*/g, ".*") + "$"
					}
					s.accept = new RegExp(n, "i")
				}
				s.queue = new e();
				s.stats = s.queue.stats;
				if(this.request("predict-runtime-type") !== "html5") {
					return
				}
				u = g.Deferred();
				o = new k("Placeholder");
				o.connectRuntime({
					runtimeOrder: "html5"
				}, function() {
					s._ruid = o.getRuid();
					u.resolve()
				});
				return u.promise()
			},
			_wrapFile: function(m) {
				if(!(m instanceof l)) {
					if(!(m instanceof i)) {
						if(!this._ruid) {
							throw new Error("Can't add external files.")
						}
						m = new i(this._ruid, m)
					}
					m = new l(m)
				}
				return m
			},
			acceptFile: function(m) {
				var n = !m || this.accept && h.exec(m.name) && !this.accept.test(m.name);
				return !n
			},
			_addFile: function(m) {
				var n = this;
				m = n._wrapFile(m);
				if(!n.owner.trigger("beforeFileQueued", m)) {
					return
				}
				if(!n.acceptFile(m)) {
					n.owner.trigger("error", "Q_TYPE_DENIED", m);
					return
				}
				n.queue.append(m);
				n.owner.trigger("fileQueued", m);
				return m
			},
			getFile: function(m) {
				return this.queue.getFile(m)
			},
			addFiles: function(n) {
				var m = this;
				if(!n.length) {
					n = [n]
				}
				n = f.map(n, function(o) {
					return m._addFile(o)
				});
				m.owner.trigger("filesQueued", n);
				if(m.options.auto) {
					setTimeout(function() {
						m.request("start-upload")
					}, 20)
				}
			},
			getStats: function() {
				return this.stats
			},
			removeFile: function(m) {
				var n = this;
				m = m.id ? m : n.queue.getFile(m);
				m.setStatus(j.CANCELLED);
				n.owner.trigger("fileDequeued", m)
			},
			getFiles: function() {
				return this.queue.getFiles.apply(this.queue, arguments)
			},
			fetchFile: function() {
				return this.queue.fetch.apply(this.queue, arguments)
			},
			retry: function(p, n) {
				var r = this,
					q, o, m;
				if(p) {
					p = p.id ? p : r.queue.getFile(p);
					p.setStatus(j.QUEUED);
					n || r.request("start-upload");
					return
				}
				q = r.queue.getFiles(j.ERROR);
				o = 0;
				m = q.length;
				for(; o < m; o++) {
					p = q[o];
					p.setStatus(j.QUEUED)
				}
				r.request("start-upload")
			},
			sortFiles: function() {
				return this.queue.sort.apply(this.queue, arguments)
			},
			reset: function() {
				this.owner.trigger("reset");
				this.queue = new e();
				this.stats = this.queue.stats
			}
		})
	});
	c("widgets/runtime", ["uploader", "runtime/runtime", "widgets/widget"], function(e, d) {
		e.support = function() {
			return d.hasRuntime.apply(d, arguments)
		};
		return e.register({
			"predict-runtime-type": "predictRuntmeType"
		}, {
			init: function() {
				if(!this.predictRuntmeType()) {
					throw Error("Runtime Error")
				}
			},
			predictRuntmeType: function() {
				var j = this.options.runtimeOrder || d.orders,
					h = this.type,
					g, f;
				if(!h) {
					j = j.split(/\s*,\s*/g);
					for(g = 0, f = j.length; g < f; g++) {
						if(d.hasRuntime(j[g])) {
							this.type = h = j[g];
							break
						}
					}
				}
				return h
			}
		})
	});
	c("lib/transport", ["base", "runtime/client", "mediator"], function(e, d, g) {
		var f = e.$;

		function h(j) {
			var i = this;
			j = i.options = f.extend(true, {}, h.options, j || {});
			d.call(this, "Transport");
			this._blob = null;
			this._formData = j.formData || {};
			this._headers = j.headers || {};
			this.on("progress", this._timeout);
			this.on("load error", function() {
				i.trigger("progress", 1);
				clearTimeout(i._timer)
			})
		}
		h.options = {
			server: "",
			method: "POST",
			withCredentials: false,
			fileVal: "file",
			timeout: 2 * 60 * 1000,
			formData: {},
			headers: {},
			sendAsBinary: false
		};
		f.extend(h.prototype, {
			appendBlob: function(k, j, i) {
				var m = this,
					l = m.options;
				if(m.getRuid()) {
					m.disconnectRuntime()
				}
				m.connectRuntime(j.ruid, function() {
					m.exec("init")
				});
				m._blob = j;
				l.fileVal = k || l.fileVal;
				l.filename = i || l.filename
			},
			append: function(i, j) {
				if(typeof i === "object") {
					f.extend(this._formData, i)
				} else {
					this._formData[i] = j
				}
			},
			setRequestHeader: function(i, j) {
				if(typeof i === "object") {
					f.extend(this._headers, i)
				} else {
					this._headers[i] = j
				}
			},
			send: function(i) {
				this.exec("send", i);
				this._timeout()
			},
			abort: function() {
				clearTimeout(this._timer);
				return this.exec("abort")
			},
			destroy: function() {
				this.trigger("destroy");
				this.off();
				this.exec("destroy");
				this.disconnectRuntime()
			},
			getResponse: function() {
				return this.exec("getResponse")
			},
			getResponseAsJson: function() {
				return this.exec("getResponseAsJson")
			},
			getStatus: function() {
				return this.exec("getStatus")
			},
			_timeout: function() {
				var i = this,
					j = i.options.timeout;
				if(!j) {
					return
				}
				clearTimeout(i._timer);
				i._timer = setTimeout(function() {
					i.abort();
					i.trigger("error", "timeout")
				}, j)
			}
		});
		g.installTo(h.prototype);
		return h
	});
	c("widgets/upload", ["base", "uploader", "file", "lib/transport", "widgets/widget"], function(f, j, i, k) {
		var h = f.$,
			e = f.isPromise,
			d = i.Status;
		h.extend(j.options, {
			prepareNextFile: false,
			chunked: false,
			chunkSize: 5 * 1024 * 1024,
			chunkRetry: 2,
			threads: 3,
			formData: null
		});

		function g(o, p) {
			var n = [],
				l = o.source,
				t = l.size,
				q = p ? Math.ceil(t / p) : 1,
				m = 0,
				s = 0,
				r;
			while(s < q) {
				r = Math.min(p, t - m);
				n.push({
					file: o,
					start: m,
					end: p ? (m + r) : t,
					total: t,
					chunks: q,
					chunk: s++
				});
				m += r
			}
			o.blocks = n.concat();
			o.remaning = n.length;
			return {
				file: o,
				has: function() {
					return !!n.length
				},
				fetch: function() {
					return n.shift()
				}
			}
		}
		j.register({
			"start-upload": "start",
			"stop-upload": "stop",
			"skip-file": "skipFile",
			"is-in-progress": "isInProgress"
		}, {
			init: function() {
				var l = this.owner;
				this.runing = false;
				this.pool = [];
				this.pending = [];
				this.remaning = 0;
				this.__tick = f.bindFn(this._tick, this);
				l.on("uploadComplete", function(m) {
					m.blocks && h.each(m.blocks, function(o, n) {
						n.transport && (n.transport.abort(), n.transport.destroy());
						delete n.transport
					});
					delete m.blocks;
					delete m.remaning
				})
			},
			start: function() {
				var l = this;
				h.each(l.request("get-files", d.INVALID), function() {
					l.request("remove-file", this)
				});
				if(l.runing) {
					return
				}
				l.runing = true;
				h.each(l.pool, function(n, m) {
					var o = m.file;
					if(o.getStatus() === d.INTERRUPT) {
						o.setStatus(d.PROGRESS);
						l._trigged = false;
						m.transport && m.transport.send()
					}
				});
				l._trigged = false;
				l.owner.trigger("startUpload");
				f.nextTick(l.__tick)
			},
			stop: function(m) {
				var l = this;
				if(l.runing === false) {
					return
				}
				l.runing = false;
				m && h.each(l.pool, function(o, n) {
					n.transport && n.transport.abort();
					n.file.setStatus(d.INTERRUPT)
				});
				l.owner.trigger("stopUpload")
			},
			isInProgress: function() {
				return !!this.runing
			},
			getStats: function() {
				return this.request("get-stats")
			},
			skipFile: function(m, l) {
				m = this.request("get-file", m);
				m.setStatus(l || d.COMPLETE);
				m.skipped = true;
				m.blocks && h.each(m.blocks, function(p, o) {
					var n = o.transport;
					if(n) {
						n.abort();
						n.destroy();
						delete o.transport
					}
				});
				this.owner.trigger("uploadSkip", m)
			},
			_tick: function() {
				var n = this,
					m = n.options,
					l, o;
				if(n._promise) {
					return n._promise.always(n.__tick)
				}
				if(n.pool.length < m.threads && (o = n._nextBlock())) {
					n._trigged = false;
					l = function(p) {
						n._promise = null;
						p && p.file && n._startSend(p);
						f.nextTick(n.__tick)
					};
					n._promise = e(o) ? o.always(l) : l(o)
				} else {
					if(!n.remaning && !n.getStats().numOfQueue) {
						n.runing = false;
						n._trigged || f.nextTick(function() {
							n.owner.trigger("uploadFinished")
						});
						n._trigged = true
					}
				}
			},
			_nextBlock: function() {
				var p = this,
					m = p._act,
					o = p.options,
					n, l;
				if(m && m.has() && m.file.getStatus() === d.PROGRESS) {
					if(o.prepareNextFile && !p.pending.length) {
						p._prepareNextFile()
					}
					return m.fetch()
				} else {
					if(p.runing) {
						if(!p.pending.length && p.getStats().numOfQueue) {
							p._prepareNextFile()
						}
						n = p.pending.shift();
						l = function(q) {
							if(!q) {
								return null
							}
							m = g(q, o.chunked ? o.chunkSize : 0);
							p._act = m;
							return m.fetch()
						};
						return e(n) ? n[n.pipe ? "pipe" : "then"](l) : l(n)
					}
				}
			},
			_prepareNextFile: function() {
				var m = this,
					l = m.request("fetch-file"),
					o = m.pending,
					n;
				if(l) {
					n = m.request("before-send-file", l, function() {
						if(l.getStatus() === d.QUEUED) {
							m.owner.trigger("uploadStart", l);
							l.setStatus(d.PROGRESS);
							return l
						}
						return m._finishFile(l)
					});
					n.done(function() {
						var p = h.inArray(n, o);
						~p && o.splice(p, 1, l)
					});
					n.fail(function(p) {
						l.setStatus(d.ERROR, p);
						m.owner.trigger("uploadError", l, p);
						m.owner.trigger("uploadComplete", l)
					});
					o.push(n)
				}
			},
			_popBlock: function(m) {
				var l = h.inArray(m, this.pool);
				this.pool.splice(l, 1);
				m.file.remaning--;
				this.remaning--
			},
			_startSend: function(o) {
				var m = this,
					l = o.file,
					n;
				m.pool.push(o);
				m.remaning++;
				o.blob = o.chunks === 1 ? l.source : l.source.slice(o.start, o.end);
				n = m.request("before-send", o, function() {
					if(l.getStatus() === d.PROGRESS) {
						m._doSend(o)
					} else {
						m._popBlock(o);
						f.nextTick(m.__tick)
					}
				});
				n.fail(function() {
					if(l.remaning === 1) {
						m._finishFile(l).always(function() {
							o.percentage = 1;
							m._popBlock(o);
							m.owner.trigger("uploadComplete", l);
							f.nextTick(m.__tick)
						})
					} else {
						o.percentage = 1;
						m._popBlock(o);
						f.nextTick(m.__tick)
					}
				})
			},
			_doSend: function(p) {
				var t = this,
					m = t.owner,
					l = t.options,
					o = p.file,
					s = new k(l),
					q = h.extend({}, l.formData),
					n = h.extend({}, l.headers),
					u, r;
				p.transport = s;
				s.on("destroy", function() {
					delete p.transport;
					t._popBlock(p);
					f.nextTick(t.__tick)
				});
				s.on("progress", function(w) {
					var x = 0,
						v = 0;
					x = p.percentage = w;
					if(p.chunks > 1) {
						h.each(o.blocks, function(z, y) {
							v += (y.percentage || 0) * (y.end - y.start)
						});
						x = v / o.size
					}
					m.trigger("uploadProgress", o, x || 0)
				});
				u = function(w) {
					var v;
					r = s.getResponseAsJson() || {};
					r._raw = s.getResponse();
					v = function(x) {
						w = x
					};
					if(!m.trigger("uploadAccept", p, r, v)) {
						w = w || "server"
					}
					return w
				};
				s.on("error", function(w, v) {
					p.retried = p.retried || 0;
					if(p.chunks > 1 && (~"http,abort".indexOf(w) || p.serverNeedRetry) && p.retried < l.chunkRetry) {
						p.retried++;
						s.send()
					} else {
						if(!v && w === "server") {
							w = u(w)
						}
						o.setStatus(d.ERROR, w);
						m.trigger("uploadError", o, w);
						m.trigger("uploadComplete", o)
					}
				});
				s.on("load", function() {
					var v;
					if((v = u())) {
						s.trigger("error", v, true);
						return
					}
					if(o.remaning === 1) {
						t._finishFile(o, r)
					} else {
						s.destroy()
					}
				});
				q = h.extend(q, {
					id: o.id,
					name: o.name,
					type: o.type,
					lastModifiedDate: o.lastModifiedDate,
					size: o.size
				});
				p.chunks > 1 && h.extend(q, {
					chunks: p.chunks,
					chunk: p.chunk
				});
				m.trigger("uploadBeforeSend", p, q, n);
				s.appendBlob(l.fileVal, p.blob, o.name);
				s.append(q);
				s.setRequestHeader(n);
				s.send()
			},
			_finishFile: function(n, m, o) {
				var l = this.owner;
				return l.request("after-send-file", arguments, function() {
					n.setStatus(d.COMPLETE);
					l.trigger("uploadSuccess", n, m, o)
				}).fail(function(p) {
					if(n.getStatus() === d.PROGRESS) {
						n.setStatus(d.ERROR, p)
					}
					l.trigger("uploadError", n, p)
				}).always(function() {
					l.trigger("uploadComplete", n)
				})
			}
		})
	});
	c("widgets/validator", ["base", "uploader", "file", "widgets/widget"], function(e, i, h) {
		var g = e.$,
			d = {},
			f;
		f = {
			addValidator: function(k, j) {
				d[k] = j
			},
			removeValidator: function(j) {
				delete d[j]
			}
		};
		i.register({
			init: function() {
				var j = this;
				e.nextTick(function() {
					g.each(d, function() {
						this.call(j.owner)
					})
				})
			}
		});
		f.addValidator("fileNumLimit", function() {
			var n = this,
				m = n.options,
				l = 0,
				j = parseInt(m.fileNumLimit, 10),
				k = true;
			if(!j) {
				return
			}
			n.on("beforeFileQueued", function(o) {
				if(l >= j && k) {
					k = false;
					this.trigger("error", "Q_EXCEED_NUM_LIMIT", j, o);
					setTimeout(function() {
						k = true
					}, 1)
				}
				return l >= j ? false : true
			});
			n.on("fileQueued", function() {
				l++
			});
			n.on("fileDequeued", function() {
				l--
			});
			n.on("uploadFinished reset", function() {
				l = 0
			})
		});
		f.addValidator("fileSizeLimit", function() {
			var n = this,
				m = n.options,
				l = 0,
				j = m.fileSizeLimit >> 0,
				k = true;
			if(!j) {
				return
			}
			n.on("beforeFileQueued", function(o) {
				var p = l + o.size > j;
				if(p && k) {
					k = false;
					this.trigger("error", "Q_EXCEED_SIZE_LIMIT", j, o);
					setTimeout(function() {
						k = true
					}, 1)
				}
				return p ? false : true
			});
			n.on("fileQueued", function(o) {
				l += o.size
			});
			n.on("fileDequeued", function(o) {
				l -= o.size
			});
			n.on("uploadFinished reset", function() {
				l = 0
			})
		});
		f.addValidator("fileSingleSizeLimit", function() {
			var l = this,
				k = l.options,
				j = k.fileSingleSizeLimit;
			if(!j) {
				return
			}
			l.on("beforeFileQueued", function(m) {
				if(m.size > j) {
					m.setStatus(h.Status.INVALID, "exceed_size");
					this.trigger("error", "F_EXCEED_SIZE", m);
					return false
				}
			})
		});
		f.addValidator("duplicate", function() {
			var m = this,
				k = m.options,
				j = {};
			if(k.duplicate) {
				return
			}

			function l(r) {
				var q = 0,
					p = 0,
					n = r.length,
					o;
				for(; p < n; p++) {
					o = r.charCodeAt(p);
					q = o + (q << 6) + (q << 16) - q
				}
				return q
			}
			m.on("beforeFileQueued", function(n) {
				var pa = n.source.source.webkitRelativePath || n.source.source.fullPath || "";
				var o = n.__hash || (n.__hash = l(n.name + n.size + n.lastModifiedDate + pa));
				if(j[o]) {
					this.trigger("error", "F_DUPLICATE", n);
					return false
				}
			});
			m.on("fileQueued", function(n) {
				var o = n.__hash;
				o && (j[o] = true)
			});
			m.on("fileDequeued", function(n) {
				var o = n.__hash;
				o && (delete j[o])
			});
			m.on("reset", function() {
				j = {}
			})
		});
		return f
	});
	c("lib/md5", ["runtime/client", "mediator"], function(e, f) {
		function d() {
			e.call(this, "Md5")
		}
		f.installTo(d.prototype);
		d.prototype.loadFromBlob = function(g) {
			var h = this;
			if(h.getRuid()) {
				h.disconnectRuntime()
			}
			h.connectRuntime(g.ruid, function() {
				h.exec("init");
				h.exec("loadFromBlob", g)
			})
		};
		d.prototype.getResult = function() {
			return this.exec("getResult")
		};
		return d
	});
	c("widgets/md5", ["base", "uploader", "lib/md5", "lib/blob", "widgets/widget"], function(e, f, d, g) {
		return f.register({
			"md5-file": "md5Blob"
		}, {
			md5Blob: function(k, m, h) {
				var l = new d(),
					j = e.Deferred(),
					i = (k instanceof g) ? k : this.request("get-file", k).source;
				l.on("progress load", function(n) {
					n = n || {};
					j.notify(n.total ? n.loaded / n.total : 1)
				});
				l.on("complete", function() {
					j.resolve(l.getResult())
				});
				l.on("error", function(n) {
					j.reject(n)
				});
				if(arguments.length > 1) {
					m = m || 0;
					h = h || 0;
					m < 0 && (m = i.size + m);
					h < 0 && (h = i.size + h);
					h = Math.min(h, i.size);
					i = i.slice(m, h)
				}
				l.loadFromBlob(i);
				return j.promise()
			}
		})
	});
	c("runtime/compbase", [], function() {
		function d(e, f) {
			this.owner = e;
			this.options = e.options;
			this.getRuntime = function() {
				return f
			};
			this.getRuid = function() {
				return f.uid
			};
			this.trigger = function() {
				return e.trigger.apply(e, arguments)
			}
		}
		return d
	});
	c("runtime/html5/runtime", ["base", "runtime/runtime", "runtime/compbase"], function(e, d, i) {
		var f = "html5",
			g = {};

		function h() {
			var k = {},
				l = this,
				j = this.destory;
			d.apply(l, arguments);
			l.type = f;
			l.exec = function(o, r) {
				var n = this,
					q = n.uid,
					p = e.slice(arguments, 2),
					m;
				if(g[o]) {
					m = k[q] = k[q] || new g[o](n, l);
					if(m[r]) {
						return m[r].apply(m, p)
					}
				}
			};
			l.destory = function() {
				return j && j.apply(this, arguments)
			}
		}
		e.inherits(d, {
			constructor: h,
			init: function() {
				var j = this;
				setTimeout(function() {
					j.trigger("ready")
				}, 1)
			}
		});
		h.register = function(l, k) {
			var j = g[l] = e.inherits(i, k);
			return j
		};
		if(b.Blob && b.FileReader && b.DataView) {
			d.addRuntime(f, h)
		}
		return h
	});
	c("runtime/html5/blob", ["runtime/html5/runtime", "lib/blob"], function(e, d) {
		return e.register("Blob", {
			slice: function(i, f) {
				var g = this.owner.source,
					h = g.slice || g.webkitSlice || g.mozSlice;
				g = h.call(g, i, f);
				return new d(this.getRuid(), g)
			}
		})
	});
	c("runtime/html5/dnd", ["base", "runtime/html5/runtime", "lib/file"], function(e, h, d) {
		var g = e.$,
			f = "webuploader-dnd-";
		return h.register("DragAndDrop", {
			init: function() {
				var i = this.elem = this.options.container;
				this.dragEnterHandler = e.bindFn(this._dragEnterHandler, this);
				this.dragOverHandler = e.bindFn(this._dragOverHandler, this);
				this.dragLeaveHandler = e.bindFn(this._dragLeaveHandler, this);
				this.dropHandler = e.bindFn(this._dropHandler, this);
				this.dndOver = false;
				i.on("dragenter", this.dragEnterHandler);
				i.on("dragover", this.dragOverHandler);
				i.on("dragleave", this.dragLeaveHandler);
				i.on("drop", this.dropHandler);
				i.on("dragenter", dragEnter);
				i.on("dragover", dragOver);
				i.on("dragleave", dragLeave);
				i.on("drop", dragDrop);
				if(this.options.disableGlobalDnd) {
					g(document).on("dragover", this.dragOverHandler);
					g(document).on("drop", this.dropHandler)
				}
			},
			_dragEnterHandler: function(l) {
				var k = this,
					j = k._denied || false,
					i;
				l = l.originalEvent || l;
				if(!k.dndOver) {
					k.dndOver = true;
					i = l.dataTransfer.items;
					if(i && i.length) {
						k._denied = j = !k.trigger("accept", i)
					}
					k.elem.addClass(f + "over");
					k.elem[j ? "addClass" : "removeClass"](f + "denied")
				}
				l.dataTransfer.dropEffect = j ? "none" : "copy";
				return false
			},
			_dragOverHandler: function(j) {
				var i = this.elem.parent().get(0);
				if(i && !g.contains(i, j.currentTarget)) {
					return false
				}
				clearTimeout(this._leaveTimer);
				this._dragEnterHandler.call(this, j);
				return false
			},
			_dragLeaveHandler: function() {
				var j = this,
					i;
				i = function() {
					j.dndOver = false;
					j.elem.removeClass(f + "over " + f + "denied")
				};
				clearTimeout(j._leaveTimer);
				j._leaveTimer = setTimeout(i, 100);
				return false
			},
			_dropHandler: function(o) {
				o.stopPropagation();
        		o.preventDefault();
				// alert(123);
				var explorer =navigator.userAgent ;
					//ie 
					if (explorer.indexOf("MSIE") >= 0) {
						// alert("ie");
					}
					//firefox 
					else if (explorer.indexOf("Firefox") >= 0) {
						// alert("Firefox");
					}
					//Chrome
					else if(explorer.indexOf("Chrome") >= 0){
						// $.Alert("当前浏览器不支持上传文件夹，请下载企业文件宝客户端");
						// return;
					}
					//Opera
					else if(explorer.indexOf("Opera") >= 0){
						// alert("Opera");
					}
					//Safari
					else if(explorer.indexOf("Safari") >= 0){
						$.Alert("当前浏览器不支持上传文件夹，请下载企业文件宝客户端");
						return;
					} 
					//Netscape
					else if(explorer.indexOf("Netscape")>= 0) { 
						// alert('Netscape'); 
					} 
					function getNodePermission(parentId,ownerBy) {
						var currOwnerId = ownerId;
						if(typeof(ownerBy)!="undefined" && ownerBy !=""){
							currOwnerId = ownerBy;
						}
						var permission = null;
						var url = ctx + "/teamspace/file/nodePermission?" + Math.random();
						var params = {
							"ownerId": currOwnerId,
							"nodeId": parentId
						};
						var flag = true;
						$.ajax({
							type: "GET",
							url: url,
							data: params,
							async: false,
							error: function (data) {
							},
							success: function (data) {
								if (typeof(data) == 'string' && data.indexOf('<html>') != -1) {
									window.location.href = ctx + "/logout";
									return;
								}
								permission = data;
							}
						});
						return permission;
					}
					var nodePermission = getNodePermission(self.parentId, self.sharedownerId);
					if(nodePermission == null || nodePermission["upload"] == 0) {
						$.Alert("没有权限");
						
					}else{
						var l = this,
						j = l.getRuid(),
						i = l.elem.parent().get(0),
						n, m;
						if(i && !g.contains(i, o.currentTarget)) {
							return false
						}
						o = o.originalEvent || o;
						n = o.dataTransfer;
						try {
							m = n.getData("text/html")
						} catch(k) {}
						if(m) {
							return
						}
						l._getTansferFiles(n, function(p) {
							l.trigger("drop", g.map(p, function(q) {
								return new d(j, q)
							}))
						});
						l.dndOver = false;
						l.elem.removeClass(f + "over");
						return false
					}
			},
			_getTansferFiles: function(q, r) {
				var m = [],
					p = [],
					o, j, k, s, l, n, t;
				o = q.items;
				j = q.files;
				if(j.length == 0) {
					return
				}
				t = !!(o && o[0].webkitGetAsEntry);
				for(l = 0, n = j.length; l < n; l++) {
					k = j[l];
					s = o && o[l];
					if(t && s.webkitGetAsEntry().isDirectory) {
						p.push(this._traverseDirectoryTree(s.webkitGetAsEntry(), m))
					} else {
						m.push(k)
					}
				}
				e.when.apply(e, p).done(function() {
					if(!m.length) {
						return
					}
					r(m)
				})
			},
			_traverseDirectoryTree: function(o, m) {
				var j = e.Deferred(),
					n = this;
				if(o.isFile) {
					o.file(function(q) {
						q.fullPath = o.fullPath;
						m.push(q);
						j.resolve()
					})
				} else {
					if(o.isDirectory) {
						var i = o.createReader();
						var l = [],
							k = [];
						var p = i.readEntries.bind(i, function(r) {
							if(r.length <= 0) {
								e.when.apply(e, l).then(function() {
									m.push.apply(m, k);
									j.resolve()
								}, j.reject);
								return
							}
							var q = r.length;
							for(var s = 0; s < q; s++) {
								l.push(n._traverseDirectoryTree(r[s], k))
							}
							p()
						});
						p();
						m.push(o)
					}
				}
				return j.promise()
			},
			destroy: function() {
				var i = this.elem;
				i.off("dragenter", this.dragEnterHandler);
				i.off("dragover", this.dragEnterHandler);
				i.off("dragleave", this.dragLeaveHandler);
				i.off("drop", this.dropHandler);
				if(this.options.disableGlobalDnd) {
					g(document).off("dragover", this.dragOverHandler);
					g(document).off("drop", this.dropHandler)
				}
			}
		})
	});
	c("runtime/html5/filepaste", ["base", "runtime/html5/runtime", "lib/file"], function(e, f, d) {
		return f.register("FilePaste", {
			init: function() {
				var n = this.options,
					m = this.elem = n.container,
					k = ".*",
					h, j, g, l;
				if(n.accept) {
					h = [];
					for(j = 0, g = n.accept.length; j < g; j++) {
						l = n.accept[j].mimeTypes;
						l && h.push(l)
					}
					if(h.length) {
						k = h.join(",");
						k = k.replace(/,/g, "|").replace(/\*/g, ".*")
					}
				}
				this.accept = k = new RegExp(k, "i");
				this.hander = e.bindFn(this._pasteHander, this);
				m.on("paste", this.hander)
			},
			_pasteHander: function(n) {
				var o = [],
					k = this.getRuid(),
					j, m, h, l, g;
				n = n.originalEvent || n;
				j = n.clipboardData.items;
				for(l = 0, g = j.length; l < g; l++) {
					m = j[l];
					if(m.kind !== "file" || !(h = m.getAsFile())) {
						continue
					}
					o.push(new d(k, h))
				}
				if(o.length) {
					n.preventDefault();
					n.stopPropagation();
					this.trigger("paste", o)
				}
			},
			destroy: function() {
				this.elem.off("paste", this.hander)
			}
		})
	});
	c("runtime/html5/filepicker", ["base", "runtime/html5/runtime"], function(d, f) {
		var e = d.$;
		return f.register("FilePicker", {
			init: function() {
				var h = this.getRuntime().getContainer(),
					o = this,
					j = o.owner,
					g = o.options,
					n = e(document.createElement("label")),
					q = e(document.createElement("input")),
                    l, k, m, p;
                    if (g.webkitdirectory == true) {
                        q.attr( 'webkitdirectory', '' );
                    }
                q.css('display', 'none');
				q.attr("type", "file");
				q.attr("name", g.name);
				q.addClass("webuploader-element-invisible");
				n.on("click", function() {
					var nodePermission = getNodePermission(self.ownerId, self.parentId, self.curUserId);
					if(nodePermission == null || nodePermission["upload"] == 0) {
						$.Tost("没有权限");
					}else{
						q.trigger("click")
					}
				});
				n.css({
					opacity: 0,
					width: "100%",
					height: "100%",
					display: "block",
					cursor: "pointer",
					background: "#ffffff"
				});
				if(g.multiple) {
					q.attr("multiple", "multiple")
				}
				if(g.accept && g.accept.length > 0) {
					l = [];
					for(k = 0, m = g.accept.length; k < m; k++) {
						l.push(g.accept[k].mimeTypes)
					}
					q.attr("accept", l.join(","))
				}
				h.append(q);
				h.append(n);
				p = function(i) {
					j.trigger(i.type)
				};
				q.on("change", function(r) {
					var i = arguments.callee,
						s;
					o.files = r.target.files;
					s = this.cloneNode(true);
					s.value = null;
					this.parentNode.replaceChild(s, this);
					q.off();
					q = e(s).on("change", i).on("mouseenter mouseleave", p);
					j.trigger("change")
				});
				n.on("mouseenter mouseleave", p)
			},
			getFiles: function() {
				return this.files
			},
			destroy: function() {}
		})
	});
	c("runtime/html5/util", ["base"], function(e) {
		var f = b.createObjectURL && b || b.URL && URL.revokeObjectURL && URL || b.webkitURL,
			g = e.noop,
			d = g;
		if(f) {
			g = function() {
				return f.createObjectURL.apply(f, arguments)
			};
			d = function() {
				return f.revokeObjectURL.apply(f, arguments)
			}
		}
		return {
			createObjectURL: g,
			revokeObjectURL: d,
			dataURL2Blob: function(j) {
				var m, o, l, k, h, n;
				n = j.split(",");
				if(~n[0].indexOf("base64")) {
					m = atob(n[1])
				} else {
					m = decodeURIComponent(n[1])
				}
				l = new ArrayBuffer(m.length);
				o = new Uint8Array(l);
				for(k = 0; k < m.length; k++) {
					o[k] = m.charCodeAt(k)
				}
				h = n[0].split(":")[1].split(";")[0];
				return this.arrayBufferToBlob(l, h)
			},
			dataURL2ArrayBuffer: function(h) {
				var k, m, j, l;
				l = h.split(",");
				if(~l[0].indexOf("base64")) {
					k = atob(l[1])
				} else {
					k = decodeURIComponent(l[1])
				}
				m = new Uint8Array(k.length);
				for(j = 0; j < k.length; j++) {
					m[j] = k.charCodeAt(j)
				}
				return m.buffer
			},
			arrayBufferToBlob: function(h, j) {
				var i = b.BlobBuilder || b.WebKitBlobBuilder,
					k;
				if(i) {
					k = new i();
					k.append(h);
					return k.getBlob(j)
				}
				return new Blob([h], j ? {
					type: j
				} : {})
			},
			canvasToDataUrl: function(h, i, j) {
				return h.toDataURL(i, j / 100)
			},
			parseMeta: function(h, i) {
				i(false, {})
			},
			updateImageHead: function(h) {
				return h
			}
		}
	});
	c("runtime/html5/imagemeta", ["runtime/html5/util"], function(e) {
		var d;
		d = {
			parsers: {
				65505: []
			},
			maxMetaDataSize: 262144,
			parse: function(h, f) {
				var i = this,
					g = new FileReader();
				g.onload = function() {
					f(false, i._parse(this.result));
					g = g.onload = g.onerror = null
				};
				g.onerror = function(j) {
					f(j.message);
					g = g.onload = g.onerror = null
				};
				h = h.slice(0, i.maxMetaDataSize);
				g.readAsArrayBuffer(h.getSource())
			},
			_parse: function(k, p) {
				if(k.byteLength < 6) {
					return
				}
				var m = new DataView(k),
					j = 2,
					g = m.byteLength - 4,
					n = j,
					o = {},
					f, h, q, l;
				if(m.getUint16(0) === 65496) {
					while(j < g) {
						f = m.getUint16(j);
						if(f >= 65504 && f <= 65519 || f === 65534) {
							h = m.getUint16(j + 2) + 2;
							if(j + h > m.byteLength) {
								break
							}
							q = d.parsers[f];
							if(!p && q) {
								for(l = 0; l < q.length; l += 1) {
									q[l].call(d, m, j, h, o)
								}
							}
							j += h;
							n = j
						} else {
							break
						}
					}
					if(n > 6) {
						if(k.slice) {
							o.imageHead = k.slice(2, n)
						} else {
							o.imageHead = new Uint8Array(k).subarray(2, n)
						}
					}
				}
				return o
			},
			updateImageHead: function(f, g) {
				var i = this._parse(f, true),
					j, h, k;
				k = 2;
				if(i.imageHead) {
					k = 2 + i.imageHead.byteLength
				}
				if(f.slice) {
					h = f.slice(k)
				} else {
					h = new Uint8Array(f).subarray(k)
				}
				j = new Uint8Array(g.byteLength + 2 + h.byteLength);
				j[0] = 255;
				j[1] = 216;
				j.set(new Uint8Array(g), 2);
				j.set(new Uint8Array(h), g.byteLength + 2);
				return j.buffer
			}
		};
		e.parseMeta = function() {
			return d.parse.apply(d, arguments)
		};
		e.updateImageHead = function() {
			return d.updateImageHead.apply(d, arguments)
		};
		return d
	});
	c("runtime/html5/imagemeta/exif", ["base", "runtime/html5/imagemeta"], function(f, e) {
		var d = {};
		d.ExifMap = function() {
			return this
		};
		d.ExifMap.prototype.map = {
			Orientation: 274
		};
		d.ExifMap.prototype.get = function(g) {
			return this[g] || this[this.map[g]]
		};
		d.exifTagTypes = {
			1: {
				getValue: function(h, g) {
					return h.getUint8(g)
				},
				size: 1
			},
			2: {
				getValue: function(h, g) {
					return String.fromCharCode(h.getUint8(g))
				},
				size: 1,
				ascii: true
			},
			3: {
				getValue: function(i, g, h) {
					return i.getUint16(g, h)
				},
				size: 2
			},
			4: {
				getValue: function(i, g, h) {
					return i.getUint32(g, h)
				},
				size: 4
			},
			5: {
				getValue: function(i, g, h) {
					return i.getUint32(g, h) / i.getUint32(g + 4, h)
				},
				size: 8
			},
			9: {
				getValue: function(i, g, h) {
					return i.getInt32(g, h)
				},
				size: 4
			},
			10: {
				getValue: function(i, g, h) {
					return i.getInt32(g, h) / i.getInt32(g + 4, h)
				},
				size: 8
			}
		};
		d.exifTagTypes[7] = d.exifTagTypes[1];
		d.getExifValue = function(r, q, l, p, j, g) {
			var s = d.exifTagTypes[p],
				h, k, t, m, o, n;
			if(!s) {
				f.log("Invalid Exif data: Invalid tag type.");
				return
			}
			h = s.size * j;
			k = h > 4 ? q + r.getUint32(l + 8, g) : (l + 8);
			if(k + h > r.byteLength) {
				f.log("Invalid Exif data: Invalid data offset.");
				return
			}
			if(j === 1) {
				return s.getValue(r, k, g)
			}
			t = [];
			for(m = 0; m < j; m += 1) {
				t[m] = s.getValue(r, k + m * s.size, g)
			}
			if(s.ascii) {
				o = "";
				for(m = 0; m < t.length; m += 1) {
					n = t[m];
					if(n === "\u0000") {
						break
					}
					o += n
				}
				return o
			}
			return t
		};
		d.parseExifTag = function(l, h, k, j, i) {
			var g = l.getUint16(k, j);
			i.exif[g] = d.getExifValue(l, h, k, l.getUint16(k + 2, j), l.getUint32(k + 4, j), j)
		};
		d.parseExifTags = function(n, k, h, m, l) {
			var o, g, j;
			if(h + 6 > n.byteLength) {
				f.log("Invalid Exif data: Invalid directory offset.");
				return
			}
			o = n.getUint16(h, m);
			g = h + 2 + 12 * o;
			if(g + 4 > n.byteLength) {
				f.log("Invalid Exif data: Invalid directory size.");
				return
			}
			for(j = 0; j < o; j += 1) {
				this.parseExifTag(n, k, h + 2 + 12 * j, m, l)
			}
			return n.getUint32(g, m)
		};
		d.parseExifData = function(m, l, i, j) {
			var h = l + 10,
				k, g;
			if(m.getUint32(l + 4) !== 1165519206) {
				return
			}
			if(h + 8 > m.byteLength) {
				f.log("Invalid Exif data: Invalid segment size.");
				return
			}
			if(m.getUint16(l + 8) !== 0) {
				f.log("Invalid Exif data: Missing byte alignment offset.");
				return
			}
			switch(m.getUint16(h)) {
				case 18761:
					k = true;
					break;
				case 19789:
					k = false;
					break;
				default:
					f.log("Invalid Exif data: Invalid byte alignment marker.");
					return
			}
			if(m.getUint16(h + 2, k) !== 42) {
				f.log("Invalid Exif data: Missing TIFF marker.");
				return
			}
			g = m.getUint32(h + 4, k);
			j.exif = new d.ExifMap();
			g = d.parseExifTags(m, h, h + g, k, j)
		};
		e.parsers[65505].push(d.parseExifData);
		return d
	});
	c("runtime/html5/jpegencoder", [], function(e, d, f) {
		function g(r) {
			var t = this;
			var J = Math.round;
			var R = Math.floor;
			var n = new Array(64);
			var Q = new Array(64);
			var X = new Array(64);
			var ae = new Array(64);
			var H;
			var o;
			var x;
			var aa;
			var P = new Array(65535);
			var s = new Array(65535);
			var V = new Array(64);
			var Y = new Array(64);
			var p = [];
			var I = 0;
			var h = 7;
			var K = new Array(64);
			var k = new Array(64);
			var ab = new Array(64);
			var l = new Array(256);
			var L = new Array(2048);
			var G;
			var U = [0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63];
			var m = [0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0];
			var i = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
			var F = [0, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125];
			var z = [1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, 129, 145, 161, 8, 35, 66, 177, 193, 21, 82, 209, 240, 36, 51, 98, 114, 130, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250];
			var E = [0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0];
			var af = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
			var u = [0, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119];
			var B = [0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, 129, 8, 20, 66, 145, 161, 177, 193, 9, 35, 51, 82, 240, 21, 98, 114, 209, 10, 22, 36, 52, 225, 37, 241, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 130, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 226, 227, 228, 229, 230, 231, 232, 233, 234, 242, 243, 244, 245, 246, 247, 248, 249, 250];

			function S(an) {
				var am = [16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14, 13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99];
				for(var al = 0; al < 64; al++) {
					var aq = R((am[al] * an + 50) / 100);
					if(aq < 1) {
						aq = 1
					} else {
						if(aq > 255) {
							aq = 255
						}
					}
					n[U[al]] = aq
				}
				var ao = [17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24, 26, 56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99];
				for(var ak = 0; ak < 64; ak++) {
					var ap = R((ao[ak] * an + 50) / 100);
					if(ap < 1) {
						ap = 1
					} else {
						if(ap > 255) {
							ap = 255
						}
					}
					Q[U[ak]] = ap
				}
				var aj = [1, 1.387039845, 1.306562965, 1.175875602, 1, 0.785694958, 0.5411961, 0.275899379];
				var ai = 0;
				for(var ar = 0; ar < 8; ar++) {
					for(var ah = 0; ah < 8; ah++) {
						X[ai] = (1 / (n[U[ai]] * aj[ar] * aj[ah] * 8));
						ae[ai] = (1 / (Q[U[ai]] * aj[ar] * aj[ah] * 8));
						ai++
					}
				}
			}

			function O(al, am) {
				var ak = 0;
				var an = 0;
				var aj = new Array();
				for(var ah = 1; ah <= 16; ah++) {
					for(var ai = 1; ai <= al[ah]; ai++) {
						aj[am[an]] = [];
						aj[am[an]][0] = ak;
						aj[am[an]][1] = ah;
						an++;
						ak++
					}
					ak *= 2
				}
				return aj
			}

			function ad() {
				H = O(m, i);
				o = O(E, af);
				x = O(F, z);
				aa = O(u, B)
			}

			function C() {
				var ai = 1;
				var ak = 2;
				for(var ah = 1; ah <= 15; ah++) {
					for(var aj = ai; aj < ak; aj++) {
						s[32767 + aj] = ah;
						P[32767 + aj] = [];
						P[32767 + aj][1] = ah;
						P[32767 + aj][0] = aj
					}
					for(var al = -(ak - 1); al <= -ai; al++) {
						s[32767 + al] = ah;
						P[32767 + al] = [];
						P[32767 + al][1] = ah;
						P[32767 + al][0] = ak - 1 + al
					}
					ai <<= 1;
					ak <<= 1
				}
			}

			function ac() {
				for(var ah = 0; ah < 256; ah++) {
					L[ah] = 19595 * ah;
					L[(ah + 256) >> 0] = 38470 * ah;
					L[(ah + 512) >> 0] = 7471 * ah + 32768;
					L[(ah + 768) >> 0] = -11059 * ah;
					L[(ah + 1024) >> 0] = -21709 * ah;
					L[(ah + 1280) >> 0] = 32768 * ah + 8421375;
					L[(ah + 1536) >> 0] = -27439 * ah;
					L[(ah + 1792) >> 0] = -5329 * ah
				}
			}

			function ag(ai) {
				var aj = ai[0];
				var ah = ai[1] - 1;
				while(ah >= 0) {
					if(aj & (1 << ah)) {
						I |= (1 << h)
					}
					ah--;
					h--;
					if(h < 0) {
						if(I == 255) {
							y(255);
							y(0)
						} else {
							y(I)
						}
						h = 7;
						I = 0
					}
				}
			}

			function y(ah) {
				p.push(l[ah])
			}

			function N(ah) {
				y((ah >> 8) & 255);
				y((ah) & 255)
			}

			function T(a5, aC) {
				var aT, aS, aR, aQ, aP, aN, aM, aK;
				var aW = 0;
				var aY;
				var aB = 8;
				var av = 64;
				for(aY = 0; aY < aB; ++aY) {
					aT = a5[aW];
					aS = a5[aW + 1];
					aR = a5[aW + 2];
					aQ = a5[aW + 3];
					aP = a5[aW + 4];
					aN = a5[aW + 5];
					aM = a5[aW + 6];
					aK = a5[aW + 7];
					var a6 = aT + aK;
					var aV = aT - aK;
					var a4 = aS + aM;
					var aX = aS - aM;
					var a3 = aR + aN;
					var aZ = aR - aN;
					var a2 = aQ + aP;
					var a0 = aQ - aP;
					var az = a6 + a2;
					var aw = a6 - a2;
					var ay = a4 + a3;
					var ax = a4 - a3;
					a5[aW] = az + ay;
					a5[aW + 4] = az - ay;
					var aH = (ax + aw) * 0.707106781;
					a5[aW + 2] = aw + aH;
					a5[aW + 6] = aw - aH;
					az = a0 + aZ;
					ay = aZ + aX;
					ax = aX + aV;
					var aD = (az - ax) * 0.382683433;
					var aG = 0.5411961 * az + aD;
					var aE = 1.306562965 * ax + aD;
					var aF = ay * 0.707106781;
					var ar = aV + aF;
					var aq = aV - aF;
					a5[aW + 5] = aq + aG;
					a5[aW + 3] = aq - aG;
					a5[aW + 1] = ar + aE;
					a5[aW + 7] = ar - aE;
					aW += 8
				}
				aW = 0;
				for(aY = 0; aY < aB; ++aY) {
					aT = a5[aW];
					aS = a5[aW + 8];
					aR = a5[aW + 16];
					aQ = a5[aW + 24];
					aP = a5[aW + 32];
					aN = a5[aW + 40];
					aM = a5[aW + 48];
					aK = a5[aW + 56];
					var au = aT + aK;
					var aA = aT - aK;
					var ao = aS + aM;
					var aI = aS - aM;
					var al = aR + aN;
					var aL = aR - aN;
					var ai = aQ + aP;
					var a1 = aQ - aP;
					var at = au + ai;
					var ah = au - ai;
					var an = ao + al;
					var ak = ao - al;
					a5[aW] = at + an;
					a5[aW + 32] = at - an;
					var ap = (ak + ah) * 0.707106781;
					a5[aW + 16] = ah + ap;
					a5[aW + 48] = ah - ap;
					at = a1 + aL;
					an = aL + aI;
					ak = aI + aA;
					var aU = (at - ak) * 0.382683433;
					var am = 0.5411961 * at + aU;
					var a8 = 1.306562965 * ak + aU;
					var aj = an * 0.707106781;
					var a7 = aA + aj;
					var aJ = aA - aj;
					a5[aW + 40] = aJ + am;
					a5[aW + 24] = aJ - am;
					a5[aW + 8] = a7 + a8;
					a5[aW + 56] = a7 - a8;
					aW++
				}
				var aO;
				for(aY = 0; aY < av; ++aY) {
					aO = a5[aY] * aC[aY];
					V[aY] = (aO > 0) ? ((aO + 0.5) | 0) : ((aO - 0.5) | 0)
				}
				return V
			}

			function Z() {
				N(65504);
				N(16);
				y(74);
				y(70);
				y(73);
				y(70);
				y(0);
				y(1);
				y(1);
				y(0);
				N(1);
				N(1);
				y(0);
				y(0)
			}

			function M(ai, ah) {
				N(65472);
				N(17);
				y(8);
				N(ah);
				N(ai);
				y(3);
				y(1);
				y(17);
				y(0);
				y(2);
				y(17);
				y(1);
				y(3);
				y(17);
				y(1)
			}

			function A() {
				N(65499);
				N(132);
				y(0);
				for(var ai = 0; ai < 64; ai++) {
					y(n[ai])
				}
				y(1);
				for(var ah = 0; ah < 64; ah++) {
					y(Q[ah])
				}
			}

			function w() {
				N(65476);
				N(418);
				y(0);
				for(var al = 0; al < 16; al++) {
					y(m[al + 1])
				}
				for(var ak = 0; ak <= 11; ak++) {
					y(i[ak])
				}
				y(16);
				for(var aj = 0; aj < 16; aj++) {
					y(F[aj + 1])
				}
				for(var ai = 0; ai <= 161; ai++) {
					y(z[ai])
				}
				y(1);
				for(var ah = 0; ah < 16; ah++) {
					y(E[ah + 1])
				}
				for(var ao = 0; ao <= 11; ao++) {
					y(af[ao])
				}
				y(17);
				for(var an = 0; an < 16; an++) {
					y(u[an + 1])
				}
				for(var am = 0; am <= 161; am++) {
					y(B[am])
				}
			}

			function v() {
				N(65498);
				N(12);
				y(3);
				y(1);
				y(0);
				y(2);
				y(17);
				y(3);
				y(17);
				y(0);
				y(63);
				y(0)
			}

			function q(al, ah, ar, ax, aw) {
				var an = aw[0];
				var aj = aw[240];
				var ak;
				var ay = 16;
				var ao = 63;
				var am = 64;
				var az = T(al, ah);
				for(var at = 0; at < am; ++at) {
					Y[U[at]] = az[at]
				}
				var av = Y[0] - ar;
				ar = Y[0];
				if(av == 0) {
					ag(ax[0])
				} else {
					ak = 32767 + av;
					ag(ax[s[ak]]);
					ag(P[ak])
				}
				var ai = 63;
				for(;
					(ai > 0) && (Y[ai] == 0); ai--) {}
				if(ai == 0) {
					ag(an);
					return ar
				}
				var au = 1;
				var aB;
				while(au <= ai) {
					var aq = au;
					for(;
						(Y[au] == 0) && (au <= ai); ++au) {}
					var ap = au - aq;
					if(ap >= ay) {
						aB = ap >> 4;
						for(var aA = 1; aA <= aB; ++aA) {
							ag(aj)
						}
						ap = ap & 15
					}
					ak = 32767 + Y[au];
					ag(aw[(ap << 4) + s[ak]]);
					ag(P[ak]);
					au++
				}
				if(ai != ao) {
					ag(an)
				}
				return ar
			}

			function D() {
				var ai = String.fromCharCode;
				for(var ah = 0; ah < 256; ah++) {
					l[ah] = ai(ah)
				}
			}
			this.encode = function(av, ap) {
				if(ap) {
					j(ap)
				}
				p = new Array();
				I = 0;
				h = 7;
				N(65496);
				Z();
				A();
				M(av.width, av.height);
				w();
				v();
				var aq = 0;
				var aw = 0;
				var au = 0;
				I = 0;
				h = 7;
				this.encode.displayName = "_encode_";
				var aC = av.data;
				var az = av.width;
				var at = av.height;
				var ay = az * 4;
				var ah = az * 3;
				var ao, an = 0;
				var ar, aB, aD;
				var ai, ax, ak, am, al;
				while(an < at) {
					ao = 0;
					while(ao < ay) {
						ai = ay * an + ao;
						ax = ai;
						ak = -1;
						am = 0;
						for(al = 0; al < 64; al++) {
							am = al >> 3;
							ak = (al & 7) * 4;
							ax = ai + (am * ay) + ak;
							if(an + am >= at) {
								ax -= (ay * (an + 1 + am - at))
							}
							if(ao + ak >= ay) {
								ax -= ((ao + ak) - ay + 4)
							}
							ar = aC[ax++];
							aB = aC[ax++];
							aD = aC[ax++];
							K[al] = ((L[ar] + L[(aB + 256) >> 0] + L[(aD + 512) >> 0]) >> 16) - 128;
							k[al] = ((L[(ar + 768) >> 0] + L[(aB + 1024) >> 0] + L[(aD + 1280) >> 0]) >> 16) - 128;
							ab[al] = ((L[(ar + 1280) >> 0] + L[(aB + 1536) >> 0] + L[(aD + 1792) >> 0]) >> 16) - 128
						}
						aq = q(K, X, aq, H, x);
						aw = q(k, ae, aw, o, aa);
						au = q(ab, ae, au, o, aa);
						ao += 32
					}
					an += 8
				}
				if(h >= 0) {
					var aA = [];
					aA[1] = h + 1;
					aA[0] = (1 << (h + 1)) - 1;
					ag(aA)
				}
				N(65497);
				var aj = "data:image/jpeg;base64," + btoa(p.join(""));
				p = [];
				return aj
			};

			function j(ai) {
				if(ai <= 0) {
					ai = 1
				}
				if(ai > 100) {
					ai = 100
				}
				if(G == ai) {
					return
				}
				var ah = 0;
				if(ai < 50) {
					ah = Math.floor(5000 / ai)
				} else {
					ah = Math.floor(200 - ai * 2)
				}
				S(ah);
				G = ai
			}

			function W() {
				if(!r) {
					r = 50
				}
				D();
				ad();
				C();
				ac();
				j(r)
			}
			W()
		}
		g.encode = function(i, j) {
			var h = new g(j);
			return h.encode(i)
		};
		return g
	});
	c("runtime/html5/androidpatch", ["runtime/html5/util", "runtime/html5/jpegencoder", "base"], function(h, g, f) {
		var d = h.canvasToDataUrl,
			e;
		h.canvasToDataUrl = function(k, m, p) {
			var j, i, l, n, o;
			if(!f.os.android) {
				return d.apply(null, arguments)
			}
			if(m === "image/jpeg" && typeof e === "undefined") {
				n = d.apply(null, arguments);
				o = n.split(",");
				if(~o[0].indexOf("base64")) {
					n = atob(o[1])
				} else {
					n = decodeURIComponent(o[1])
				}
				n = n.substring(0, 2);
				e = n.charCodeAt(0) === 255 && n.charCodeAt(1) === 216
			}
			if(m === "image/jpeg" && !e) {
				i = k.width;
				l = k.height;
				j = k.getContext("2d");
				return g.encode(j.getImageData(0, 0, i, l), p)
			}
			return d.apply(null, arguments)
		}
	});
	c("runtime/html5/image", ["base", "runtime/html5/runtime", "runtime/html5/util"], function(d, g, e) {
		var f = "data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs%3D";
		return g.register("Image", {
			modified: false,
			init: function() {
				var i = this,
					h = new Image();
				h.onload = function() {
					i._info = {
						type: i.type,
						width: this.width,
						height: this.height
					};
					if(!i._metas && "image/jpeg" === i.type) {
						e.parseMeta(i._blob, function(k, j) {
							i._metas = j;
							i.owner.trigger("load")
						})
					} else {
						i.owner.trigger("load")
					}
				};
				h.onerror = function() {
					i.owner.trigger("error")
				};
				i._img = h
			},
			loadFromBlob: function(i) {
				var j = this,
					h = j._img;
				j._blob = i;
				j.type = i.type;
				h.src = e.createObjectURL(i.getSource());
				j.owner.once("load", function() {
					e.revokeObjectURL(h.src)
				})
			},
			resize: function(j, h) {
				var i = this._canvas || (this._canvas = document.createElement("canvas"));
				this._resize(this._img, i, j, h);
				this._blob = null;
				this.modified = true;
				this.owner.trigger("complete", "resize")
			},
			crop: function(p, o, q, n, t) {
				var m = this._canvas || (this._canvas = document.createElement("canvas")),
					i = this.options,
					l = this._img,
					k = l.naturalWidth,
					r = l.naturalHeight,
					j = this.getOrientation();
				t = t || 1;
				m.width = q;
				m.height = n;
				i.preserveHeaders || this._rotate2Orientaion(m, j);
				this._renderImageToCanvas(m, l, -p, -o, k * t, r * t);
				this._blob = null;
				this.modified = true;
				this.owner.trigger("complete", "crop")
			},
			getAsBlob: function(j) {
				var h = this._blob,
					k = this.options,
					i;
				j = j || this.type;
				if(this.modified || this.type !== j) {
					i = this._canvas;
					if(j === "image/jpeg") {
						h = e.canvasToDataUrl(i, j, k.quality);
						if(k.preserveHeaders && this._metas && this._metas.imageHead) {
							h = e.dataURL2ArrayBuffer(h);
							h = e.updateImageHead(h, this._metas.imageHead);
							h = e.arrayBufferToBlob(h, j);
							return h
						}
					} else {
						h = e.canvasToDataUrl(i, j)
					}
					h = e.dataURL2Blob(h)
				}
				return h
			},
			getAsDataUrl: function(h) {
				var i = this.options;
				h = h || this.type;
				if(h === "image/jpeg") {
					return e.canvasToDataUrl(this._canvas, h, i.quality)
				} else {
					return this._canvas.toDataURL(h)
				}
			},
			getOrientation: function() {
				return this._metas && this._metas.exif && this._metas.exif.get("Orientation") || 1
			},
			info: function(h) {
				if(h) {
					this._info = h;
					return this
				}
				return this._info
			},
			meta: function(h) {
				if(h) {
					this._meta = h;
					return this
				}
				return this._meta
			},
			destroy: function() {
				var h = this._canvas;
				this._img.onload = null;
				if(h) {
					h.getContext("2d").clearRect(0, 0, h.width, h.height);
					h.width = h.height = 0;
					this._canvas = null
				}
				this._img.src = f;
				this._img = this._blob = null
			},
			_resize: function(m, o, j, u) {
				var i = this.options,
					p = m.width,
					t = m.height,
					k = this.getOrientation(),
					l, s, n, r, q;
				if(~[5, 6, 7, 8].indexOf(k)) {
					j ^= u;
					u ^= j;
					j ^= u
				}
				l = Math[i.crop ? "max" : "min"](j / p, u / t);
				i.allowMagnify || (l = Math.min(1, l));
				s = p * l;
				n = t * l;
				if(i.crop) {
					o.width = j;
					o.height = u
				} else {
					o.width = s;
					o.height = n
				}
				r = (o.width - s) / 2;
				q = (o.height - n) / 2;
				i.preserveHeaders || this._rotate2Orientaion(o, k);
				this._renderImageToCanvas(o, m, r, q, s, n)
			},
			_rotate2Orientaion: function(k, j) {
				var l = k.width,
					h = k.height,
					i = k.getContext("2d");
				switch(j) {
					case 5:
					case 6:
					case 7:
					case 8:
						k.width = h;
						k.height = l;
						break
				}
				switch(j) {
					case 2:
						i.translate(l, 0);
						i.scale(-1, 1);
						break;
					case 3:
						i.translate(l, h);
						i.rotate(Math.PI);
						break;
					case 4:
						i.translate(0, h);
						i.scale(1, -1);
						break;
					case 5:
						i.rotate(0.5 * Math.PI);
						i.scale(1, -1);
						break;
					case 6:
						i.rotate(0.5 * Math.PI);
						i.translate(0, -h);
						break;
					case 7:
						i.rotate(0.5 * Math.PI);
						i.translate(l, -h);
						i.scale(-1, 1);
						break;
					case 8:
						i.rotate(-0.5 * Math.PI);
						i.translate(-l, 0);
						break
				}
			},
			_renderImageToCanvas: (function() {
				if(!d.os.ios) {
					return function(l) {
						var k = d.slice(arguments, 1),
							j = l.getContext("2d");
						j.drawImage.apply(j, k)
					}
				}

				function i(n, k, s) {
					var j = document.createElement("canvas"),
						t = j.getContext("2d"),
						q = 0,
						o = s,
						r = s,
						m, l, p;
					j.width = 1;
					j.height = s;
					t.drawImage(n, 0, 0);
					m = t.getImageData(0, 0, 1, s).data;
					while(r > q) {
						l = m[(r - 1) * 4 + 3];
						if(l === 0) {
							o = r
						} else {
							q = r
						}
						r = (o + q) >> 1
					}
					p = (r / s);
					return(p === 0) ? 1 : p
				}
				if(d.os.ios >= 7) {
					return function(j, l, p, o, q, m) {
						var k = l.naturalWidth,
							r = l.naturalHeight,
							n = i(l, k, r);
						return j.getContext("2d").drawImage(l, 0, 0, k * n, r * n, p, o, q, m)
					}
				}

				function h(l) {
					var k = l.naturalWidth,
						n = l.naturalHeight,
						m, j;
					if(k * n > 1024 * 1024) {
						m = document.createElement("canvas");
						m.width = m.height = 1;
						j = m.getContext("2d");
						j.drawImage(l, -k + 1, 0);
						return j.getImageData(0, 0, 1, 1).data[3] === 0
					} else {
						return false
					}
				}
				return function(m, F, q, p, A, z) {
					var o = F.naturalWidth,
						u = F.naturalHeight,
						B = m.getContext("2d"),
						l = h(F),
						j = this.type === "image/jpeg",
						E = 1024,
						v = 0,
						r = 0,
						k, n, C, t, D, w, s;
					if(l) {
						o /= 2;
						u /= 2
					}
					B.save();
					k = document.createElement("canvas");
					k.width = k.height = E;
					n = k.getContext("2d");
					C = j ? i(F, o, u) : 1;
					t = Math.ceil(E * A / o);
					D = Math.ceil(E * z / u / C);
					while(v < u) {
						w = 0;
						s = 0;
						while(w < o) {
							n.clearRect(0, 0, E, E);
							n.drawImage(F, -w, -v);
							B.drawImage(k, 0, 0, E, E, q + s, p + r, t, D);
							w += E;
							s += t
						}
						v += E;
						r += D
					}
					B.restore();
					k = n = null
				}
			})()
		})
	});
	c("runtime/html5/transport", ["base", "runtime/html5/runtime"], function(d, g) {
		var e = d.noop,
			f = d.$;
		return g.register("Transport", {
			init: function() {
				this._status = 0;
				this._response = null
			},
			send: function() {
				var h = this.owner,
					k = this.options,
					n = this._initAjax(),
					j = h._blob,
					m = k.server,
					l, o, i;
				if(k.sendAsBinary) {
					m += (/\?/.test(m) ? "&" : "?") + f.param(h._formData);
					o = j.getSource()
				} else {
					l = new FormData();
					f.each(h._formData, function(q, p) {
						l.append(q, p)
					});
					l.append(k.fileVal, j.getSource(), k.filename || h._formData.name || "")
				}
				if(k.withCredentials && "withCredentials" in n) {
					n.open(k.method, m, true);
					n.withCredentials = true
				} else {
					n.open(k.method, m)
				}
				this._setRequestHeader(n, k.headers);
				if(o) {
					n.overrideMimeType("application/octet-stream");
					if(d.os.android) {
						i = new FileReader();
						i.onload = function() {
							n.send(this.result);
							i = i.onload = null
						};
						i.readAsArrayBuffer(o)
					} else {
						n.send(o)
					}
				} else {
					n.send(l)
				}
			},
			getResponse: function() {
				return this._response
			},
			getResponseAsJson: function() {
				return this._parseJson(this._response)
			},
			getStatus: function() {
				return this._status
			},
			abort: function() {
				var h = this._xhr;
				if(h) {
					h.upload.onprogress = e;
					h.onreadystatechange = e;
					h.abort();
					this._xhr = h = null
				}
			},
			destroy: function() {
				this.abort()
			},
			_initAjax: function() {
				var i = this,
					j = new XMLHttpRequest(),
					h = this.options;
				if(h.withCredentials && !("withCredentials" in j) && typeof XDomainRequest !== "undefined") {
					j = new XDomainRequest()
				}
				j.upload.onprogress = function(l) {
					var k = 0;
					if(l.lengthComputable) {
						k = l.loaded / l.total
					}
					return i.trigger("progress", k)
				};
				j.onreadystatechange = function() {
					if(j.readyState !== 4) {
						return
					}
					j.upload.onprogress = e;
					j.onreadystatechange = e;
					i._xhr = null;
					i._status = j.status;
					if(j.status >= 200 && j.status < 300) {
						i._response = j.responseText;
						return i.trigger("load")
					} else {
						if(j.status >= 500 && j.status < 600) {
							i._response = j.responseText;
							return i.trigger("error", "server")
						}
					}
					return i.trigger("error", i._status ? "http" : "abort")
				};
				i._xhr = j;
				return j
			},
			_setRequestHeader: function(i, h) {
				f.each(h, function(j, k) {
					i.setRequestHeader(j, k)
				})
			},
			_parseJson: function(j) {
				var i;
				try {
					i = JSON.parse(j)
				} catch(h) {
					i = {}
				}
				return i
			}
		})
	});
	c("runtime/html5/md5", ["runtime/html5/runtime"], function(p) {
		var g = function(v, u) {
				return(v + u) & 4294967295
			},
			q = function(A, w, v, u, z, y) {
				w = g(g(w, A), g(u, y));
				return g((w << z) | (w >>> (32 - z)), v)
			},
			d = function(w, v, B, A, u, z, y) {
				return q((v & B) | ((~v) & A), w, v, u, z, y)
			},
			m = function(w, v, B, A, u, z, y) {
				return q((v & A) | (B & (~A)), w, v, u, z, y)
			},
			h = function(w, v, B, A, u, z, y) {
				return q(v ^ B ^ A, w, v, u, z, y)
			},
			s = function(w, v, B, A, u, z, y) {
				return q(B ^ (v | (~A)), w, v, u, z, y)
			},
			f = function(v, y) {
				var w = v[0],
					u = v[1],
					A = v[2],
					z = v[3];
				w = d(w, u, A, z, y[0], 7, -680876936);
				z = d(z, w, u, A, y[1], 12, -389564586);
				A = d(A, z, w, u, y[2], 17, 606105819);
				u = d(u, A, z, w, y[3], 22, -1044525330);
				w = d(w, u, A, z, y[4], 7, -176418897);
				z = d(z, w, u, A, y[5], 12, 1200080426);
				A = d(A, z, w, u, y[6], 17, -1473231341);
				u = d(u, A, z, w, y[7], 22, -45705983);
				w = d(w, u, A, z, y[8], 7, 1770035416);
				z = d(z, w, u, A, y[9], 12, -1958414417);
				A = d(A, z, w, u, y[10], 17, -42063);
				u = d(u, A, z, w, y[11], 22, -1990404162);
				w = d(w, u, A, z, y[12], 7, 1804603682);
				z = d(z, w, u, A, y[13], 12, -40341101);
				A = d(A, z, w, u, y[14], 17, -1502002290);
				u = d(u, A, z, w, y[15], 22, 1236535329);
				w = m(w, u, A, z, y[1], 5, -165796510);
				z = m(z, w, u, A, y[6], 9, -1069501632);
				A = m(A, z, w, u, y[11], 14, 643717713);
				u = m(u, A, z, w, y[0], 20, -373897302);
				w = m(w, u, A, z, y[5], 5, -701558691);
				z = m(z, w, u, A, y[10], 9, 38016083);
				A = m(A, z, w, u, y[15], 14, -660478335);
				u = m(u, A, z, w, y[4], 20, -405537848);
				w = m(w, u, A, z, y[9], 5, 568446438);
				z = m(z, w, u, A, y[14], 9, -1019803690);
				A = m(A, z, w, u, y[3], 14, -187363961);
				u = m(u, A, z, w, y[8], 20, 1163531501);
				w = m(w, u, A, z, y[13], 5, -1444681467);
				z = m(z, w, u, A, y[2], 9, -51403784);
				A = m(A, z, w, u, y[7], 14, 1735328473);
				u = m(u, A, z, w, y[12], 20, -1926607734);
				w = h(w, u, A, z, y[5], 4, -378558);
				z = h(z, w, u, A, y[8], 11, -2022574463);
				A = h(A, z, w, u, y[11], 16, 1839030562);
				u = h(u, A, z, w, y[14], 23, -35309556);
				w = h(w, u, A, z, y[1], 4, -1530992060);
				z = h(z, w, u, A, y[4], 11, 1272893353);
				A = h(A, z, w, u, y[7], 16, -155497632);
				u = h(u, A, z, w, y[10], 23, -1094730640);
				w = h(w, u, A, z, y[13], 4, 681279174);
				z = h(z, w, u, A, y[0], 11, -358537222);
				A = h(A, z, w, u, y[3], 16, -722521979);
				u = h(u, A, z, w, y[6], 23, 76029189);
				w = h(w, u, A, z, y[9], 4, -640364487);
				z = h(z, w, u, A, y[12], 11, -421815835);
				A = h(A, z, w, u, y[15], 16, 530742520);
				u = h(u, A, z, w, y[2], 23, -995338651);
				w = s(w, u, A, z, y[0], 6, -198630844);
				z = s(z, w, u, A, y[7], 10, 1126891415);
				A = s(A, z, w, u, y[14], 15, -1416354905);
				u = s(u, A, z, w, y[5], 21, -57434055);
				w = s(w, u, A, z, y[12], 6, 1700485571);
				z = s(z, w, u, A, y[3], 10, -1894986606);
				A = s(A, z, w, u, y[10], 15, -1051523);
				u = s(u, A, z, w, y[1], 21, -2054922799);
				w = s(w, u, A, z, y[8], 6, 1873313359);
				z = s(z, w, u, A, y[15], 10, -30611744);
				A = s(A, z, w, u, y[6], 15, -1560198380);
				u = s(u, A, z, w, y[13], 21, 1309151649);
				w = s(w, u, A, z, y[4], 6, -145523070);
				z = s(z, w, u, A, y[11], 10, -1120210379);
				A = s(A, z, w, u, y[2], 15, 718787259);
				u = s(u, A, z, w, y[9], 21, -343485551);
				v[0] = g(w, v[0]);
				v[1] = g(u, v[1]);
				v[2] = g(A, v[2]);
				v[3] = g(z, v[3])
			},
			t = function(v) {
				var w = [],
					u;
				for(u = 0; u < 64; u += 4) {
					w[u >> 2] = v.charCodeAt(u) + (v.charCodeAt(u + 1) << 8) + (v.charCodeAt(u + 2) << 16) + (v.charCodeAt(u + 3) << 24)
				}
				return w
			},
			o = function(u) {
				var w = [],
					v;
				for(v = 0; v < 64; v += 4) {
					w[v >> 2] = u[v] + (u[v + 1] << 8) + (u[v + 2] << 16) + (u[v + 3] << 24)
				}
				return w
			},
			n = function(C) {
				var w = C.length,
					u = [1732584193, -271733879, -1732584194, 271733878],
					y, v, B, z, A, x;
				for(y = 64; y <= w; y += 64) {
					f(u, t(C.substring(y - 64, y)))
				}
				C = C.substring(y - 64);
				v = C.length;
				B = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
				for(y = 0; y < v; y += 1) {
					B[y >> 2] |= C.charCodeAt(y) << ((y % 4) << 3)
				}
				B[y >> 2] |= 128 << ((y % 4) << 3);
				if(y > 55) {
					f(u, B);
					for(y = 0; y < 16; y += 1) {
						B[y] = 0
					}
				}
				z = w * 8;
				z = z.toString(16).match(/(.*?)(.{0,8})$/);
				A = parseInt(z[2], 16);
				x = parseInt(z[1], 16) || 0;
				B[14] = A;
				B[15] = x;
				f(u, B);
				return u
			},
			r = function(C) {
				var w = C.length,
					u = [1732584193, -271733879, -1732584194, 271733878],
					y, v, B, z, A, x;
				for(y = 64; y <= w; y += 64) {
					f(u, o(C.subarray(y - 64, y)))
				}
				C = (y - 64) < w ? C.subarray(y - 64) : new Uint8Array(0);
				v = C.length;
				B = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
				for(y = 0; y < v; y += 1) {
					B[y >> 2] |= C[y] << ((y % 4) << 3)
				}
				B[y >> 2] |= 128 << ((y % 4) << 3);
				if(y > 55) {
					f(u, B);
					for(y = 0; y < 16; y += 1) {
						B[y] = 0
					}
				}
				z = w * 8;
				z = z.toString(16).match(/(.*?)(.{0,8})$/);
				A = parseInt(z[2], 16);
				x = parseInt(z[1], 16) || 0;
				B[14] = A;
				B[15] = x;
				f(u, B);
				return u
			},
			l = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"],
			j = function(w) {
				var v = "",
					u;
				for(u = 0; u < 4; u += 1) {
					v += l[(w >> (u * 8 + 4)) & 15] + l[(w >> (u * 8)) & 15]
				}
				return v
			},
			e = function(u) {
				var v;
				for(v = 0; v < u.length; v += 1) {
					u[v] = j(u[v])
				}
				return u.join("")
			},
			k = function(u) {
				return e(n(u))
			},
			i = function() {
				this.reset()
			};
		if(k("hello") !== "5d41402abc4b2a76b9719d911017c592") {
			g = function(u, z) {
				var w = (u & 65535) + (z & 65535),
					v = (u >> 16) + (z >> 16) + (w >> 16);
				return(v << 16) | (w & 65535)
			}
		}
		i.prototype.append = function(u) {
			if(/[\u0080-\uFFFF]/.test(u)) {
				u = unescape(encodeURIComponent(u))
			}
			this.appendBinary(u);
			return this
		};
		i.prototype.appendBinary = function(w) {
			this._buff += w;
			this._length += w.length;
			var v = this._buff.length,
				u;
			for(u = 64; u <= v; u += 64) {
				f(this._state, t(this._buff.substring(u - 64, u)))
			}
			this._buff = this._buff.substr(u - 64);
			return this
		};
		i.prototype.end = function(w) {
			var z = this._buff,
				y = z.length,
				x, v = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
				u;
			for(x = 0; x < y; x += 1) {
				v[x >> 2] |= z.charCodeAt(x) << ((x % 4) << 3)
			}
			this._finish(v, y);
			u = !!w ? this._state : e(this._state);
			this.reset();
			return u
		};
		i.prototype._finish = function(v, z) {
			var x = z,
				w, y, u;
			v[x >> 2] |= 128 << ((x % 4) << 3);
			if(x > 55) {
				f(this._state, v);
				for(x = 0; x < 16; x += 1) {
					v[x] = 0
				}
			}
			w = this._length * 8;
			w = w.toString(16).match(/(.*?)(.{0,8})$/);
			y = parseInt(w[2], 16);
			u = parseInt(w[1], 16) || 0;
			v[14] = y;
			v[15] = u;
			f(this._state, v)
		};
		i.prototype.reset = function() {
			this._buff = "";
			this._length = 0;
			this._state = [1732584193, -271733879, -1732584194, 271733878];
			return this
		};
		i.prototype.destroy = function() {
			delete this._state;
			delete this._buff;
			delete this._length
		};
		i.hash = function(w, u) {
			if(/[\u0080-\uFFFF]/.test(w)) {
				w = unescape(encodeURIComponent(w))
			}
			var v = n(w);
			return !!u ? v : e(v)
		};
		i.hashBinary = function(v, u) {
			var w = n(v);
			return !!u ? w : e(w)
		};
		i.ArrayBuffer = function() {
			this.reset()
		};
		i.ArrayBuffer.prototype.append = function(u) {
			var x = this._concatArrayBuffer(this._buff, u),
				w = x.length,
				v;
			this._length += u.byteLength;
			for(v = 64; v <= w; v += 64) {
				f(this._state, o(x.subarray(v - 64, v)))
			}
			this._buff = (v - 64) < w ? x.subarray(v - 64) : new Uint8Array(0);
			return this
		};
		i.ArrayBuffer.prototype.end = function(w) {
			var z = this._buff,
				y = z.length,
				v = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
				x, u;
			for(x = 0; x < y; x += 1) {
				v[x >> 2] |= z[x] << ((x % 4) << 3)
			}
			this._finish(v, y);
			u = !!w ? this._state : e(this._state);
			this.reset();
			return u
		};
		i.ArrayBuffer.prototype._finish = i.prototype._finish;
		i.ArrayBuffer.prototype.reset = function() {
			this._buff = new Uint8Array(0);
			this._length = 0;
			this._state = [1732584193, -271733879, -1732584194, 271733878];
			return this
		};
		i.ArrayBuffer.prototype.destroy = i.prototype.destroy;
		i.ArrayBuffer.prototype._concatArrayBuffer = function(x, v) {
			var w = x.length,
				u = new Uint8Array(w + v.byteLength);
			u.set(x);
			u.set(new Uint8Array(v), w);
			return u
		};
		i.ArrayBuffer.hash = function(u, v) {
			var w = r(new Uint8Array(u));
			return !!v ? w : e(w)
		};
		return p.register("Md5", {
			init: function() {},
			loadFromBlob: function(w) {
				var u = w.getSource(),
					x = 2 * 1024 * 1024,
					y = Math.ceil(u.size / x),
					D = 0,
					v = this.owner,
					A = new i.ArrayBuffer(),
					C = this,
					E = u.mozSlice || u.webkitSlice || u.slice,
					B, z;
				z = new FileReader();
				B = function() {
					var G, F;
					G = D * x;
					F = Math.min(G + x, u.size);
					z.onload = function(H) {
						A.append(H.target.result);
						v.trigger("progress", {
							total: w.size,
							loaded: F
						})
					};
					z.onloadend = function() {
						z.onloadend = z.onload = null;
						if(++D < y) {
							setTimeout(B, 1)
						} else {
							setTimeout(function() {
								v.trigger("load");
								C.result = A.end();
								B = w = u = A = null;
								v.trigger("complete")
							}, 50)
						}
					};
					z.readAsArrayBuffer(E.call(u, G, F))
				};
				B()
			},
			getResult: function() {
				return this.result
			}
		})
	});
	c("runtime/flash/runtime", ["base", "runtime/runtime", "runtime/compbase"], function(f, e, k) {
		var i = f.$,
			g = "flash",
			h = {};

		function d() {
			var l;
			try {
				l = navigator.plugins["Shockwave Flash"];
				l = l.description
			} catch(m) {
				try {
					l = new ActiveXObject("ShockwaveFlash.ShockwaveFlash").GetVariable("$version")
				} catch(n) {
					l = "0.0"
				}
			}
			l = l.match(/\d+/g);
			return parseFloat(l[0] + "." + l[1], 10)
		}

		function j() {
			var o = {},
				n = {},
				l = this.destory,
				q = this,
				m = f.guid("webuploader_");
			e.apply(q, arguments);
			q.type = g;
			q.exec = function(t, w) {
				var s = this,
					v = s.uid,
					u = f.slice(arguments, 2),
					r;
				n[v] = s;
				if(h[t]) {
					if(!o[v]) {
						o[v] = new h[t](s, q)
					}
					r = o[v];
					if(r[w]) {
						return r[w].apply(r, u)
					}
				}
				return q.flashExec.apply(s, arguments)
			};

			function p(r, v) {
				var t = r.type || r,
					u, s;
				u = t.split("::");
				s = u[0];
				t = u[1];
				if(t === "Ready" && s === q.uid) {
					q.trigger("ready")
				} else {
					if(n[s]) {
						n[s].trigger(t.toLowerCase(), r, v)
					}
				}
			}
			b[m] = function() {
				var r = arguments;
				setTimeout(function() {
					p.apply(null, r)
				}, 1)
			};
			this.jsreciver = m;
			this.destory = function() {
				return l && l.apply(this, arguments)
			};
			this.flashExec = function(r, u) {
				var t = q.getFlash(),
					s = f.slice(arguments, 2);
				return t.exec(this.uid, r, u, s)
			}
		}
		f.inherits(e, {
			constructor: j,
			init: function() {
				var l = this.getContainer(),
					n = this.options,
					m;
				l.css({
					position: "absolute",
					top: "-8px",
					left: "-8px",
					width: "9px",
					height: "9px",
					overflow: "hidden"
				});
				m = '<object id="' + this.uid + '" type="application/x-shockwave-flash" data="' + n.swf + '" ';
				if(f.browser.ie) {
					m += 'classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" '
				}
				m += 'width="100%" height="100%" style="outline:0"><param name="movie" value="' + n.swf + '" /><param name="flashvars" value="uid=' + this.uid + "&jsreciver=" + this.jsreciver + '" /><param name="wmode" value="transparent" /><param name="allowscriptaccess" value="always" /></object>';
				l.html(m)
			},
			getFlash: function() {
				if(this._flash) {
					return this._flash
				}
				this._flash = i("#" + this.uid).get(0);
				return this._flash
			}
		});
		j.register = function(m, l) {
			l = h[m] = f.inherits(k, i.extend({
				flashExec: function() {
					var n = this.owner,
						o = this.getRuntime();
					return o.flashExec.apply(n, arguments)
				}
			}, l));
			return l
		};
		if(d() >= 11.4) {
			e.addRuntime(g, j)
		}
		return j
	});
	c("runtime/flash/filepicker", ["base", "runtime/flash/runtime"], function(d, f) {
		var e = d.$;
		return f.register("FilePicker", {
			init: function(j) {
				var k = e.extend({}, j),
					g, h;
				g = k.accept && k.accept.length;
				for(h = 0; h < g; h++) {
					if(!k.accept[h].title) {
						k.accept[h].title = "Files"
					}
				}
				delete k.button;
				delete k.id;
				delete k.container;
				this.flashExec("FilePicker", "init", k)
			},
			destroy: function() {}
		})
	});
	c("runtime/flash/image", ["runtime/flash/runtime"], function(d) {
		return d.register("Image", {
			loadFromBlob: function(f) {
				var e = this.owner;
				e.info() && this.flashExec("Image", "info", e.info());
				e.meta() && this.flashExec("Image", "meta", e.meta());
				this.flashExec("Image", "loadFromBlob", f.uid)
			}
		})
	});
	c("runtime/flash/transport", ["base", "runtime/flash/runtime", "runtime/client"], function(e, g, d) {
		var f = e.$;
		return g.register("Transport", {
			init: function() {
				this._status = 0;
				this._response = null;
				this._responseJson = null
			},
			send: function() {
				var h = this.owner,
					j = this.options,
					l = this._initAjax(),
					i = h._blob,
					k = j.server,
					m;
				l.connectRuntime(i.ruid);
				if(j.sendAsBinary) {
					k += (/\?/.test(k) ? "&" : "?") + f.param(h._formData);
					m = i.uid
				} else {
					f.each(h._formData, function(o, n) {
						l.exec("append", o, n)
					});
					l.exec("appendBlob", j.fileVal, i.uid, j.filename || h._formData.name || "")
				}
				this._setRequestHeader(l, j.headers);
				l.exec("send", {
					method: j.method,
					url: k,
					mimeType: "application/octet-stream"
				}, m)
			},
			getStatus: function() {
				return this._status
			},
			getResponse: function() {
				return this._response || ""
			},
			getResponseAsJson: function() {
				return this._responseJson
			},
			abort: function() {
				var h = this._xhr;
				if(h) {
					h.exec("abort");
					h.destroy();
					this._xhr = h = null
				}
			},
			destroy: function() {
				this.abort()
			},
			_initAjax: function() {
				var h = this,
					i = new d("XMLHttpRequest");
				i.on("uploadprogress progress", function(k) {
					var j = k.loaded / k.total;
					j = Math.min(1, Math.max(0, j));
					return h.trigger("progress", j)
				});
				i.on("load", function() {
					var j = i.exec("getStatus"),
						k = "";
					i.off();
					h._xhr = null;
					if(j >= 200 && j < 300) {
						h._response = i.exec("getResponse");
						h._responseJson = i.exec("getResponseAsJson")
					} else {
						if(j >= 500 && j < 600) {
							h._response = i.exec("getResponse");
							h._responseJson = i.exec("getResponseAsJson");
							k = "server"
						} else {
							k = "http"
						}
					}
					h._response = decodeURIComponent(h._response);
					i.destroy();
					i = null;
					return k ? h.trigger("error", k) : h.trigger("load")
				});
				i.on("error", function() {
					i.off();
					h._xhr = null;
					h.trigger("error", "http")
				});
				h._xhr = i;
				return i
			},
			_setRequestHeader: function(i, h) {
				f.each(h, function(j, k) {
					i.exec("setRequestHeader", j, k)
				})
			}
		})
	});
	c("runtime/flash/md5", ["runtime/flash/runtime"], function(d) {
		return d.register("Md5", {
			init: function() {},
			loadFromBlob: function(e) {
				return this.flashExec("Md5", "loadFromBlob", e.uid)
			}
		})
	});
	c("preset/all", ["base", "widgets/filednd", "widgets/filepaste", "widgets/filepicker", "widgets/image", "widgets/queue", "widgets/runtime", "widgets/upload", "widgets/validator", "widgets/md5", "runtime/html5/blob", "runtime/html5/dnd", "runtime/html5/filepaste", "runtime/html5/filepicker", "runtime/html5/imagemeta/exif", "runtime/html5/androidpatch", "runtime/html5/image", "runtime/html5/transport", "runtime/html5/md5", "runtime/flash/filepicker", "runtime/flash/image", "runtime/flash/transport", "runtime/flash/md5"], function(d) {
		return d
	});
	c("webuploader", ["preset/all"], function(d) {
		return d
	});
	return a("webuploader")
});