package pw.cdmi.box.disk.share.service;

import pw.cdmi.box.disk.share.domain.MySharesPage;
import pw.cdmi.box.disk.share.domain.RestListShareResourceRequestV2;
import pw.cdmi.core.exception.RestException;

public interface ShareByMeService
{
    MySharesPage getMySharesPage(String token, RestListShareResourceRequestV2 req) throws RestException;
}
