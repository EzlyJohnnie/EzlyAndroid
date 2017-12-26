package com.ezly.ezly_android.wxapi;

import com.ezly.ezly_android.UI.BaseComponent.EzlyBaseActivity;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;

/**
 * Created by Johnnie on 11/11/16.
 */

public class WXEntryActivity extends EzlyBaseActivity  {

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                getMemberHelper().onWeChatLoginDenied();
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                getMemberHelper().onWeChatLoginCancelled();
                break;

            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        String code = ((SendAuth.Resp) resp).code;
                        getMemberHelper().getWeChatAccessToken(code);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        //TODO: Share callback
                        break;
                }
                break;
        }
        finish();
    }
}
