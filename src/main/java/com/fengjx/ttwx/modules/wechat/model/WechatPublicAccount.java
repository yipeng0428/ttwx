
package com.fengjx.ttwx.modules.wechat.model;

import com.fengjx.ttwx.common.plugin.db.Mapper;
import com.fengjx.ttwx.common.plugin.db.Model;
import com.fengjx.ttwx.common.plugin.db.Record;
import com.fengjx.ttwx.common.utils.AesUtil;
import com.fengjx.ttwx.common.utils.CommonUtils;
import com.fengjx.ttwx.modules.common.constants.AppConfig;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号管理
 *
 * @author fengjx.
 * @date：2015/5/17 0017
 */
@Component
@Mapper(table = "wechat_public_account")
public class WechatPublicAccount extends Model {

    public static final String VALID_STATE_NONACTIVATED = "0"; // 0：未激活
    public static final String VALID_STATE_EXCESS = "1"; // 1：已配置到公众平台
    public static final String VALID_STATE_ACTIVATE = "2"; // 2：已通过客户端校验验证码

    @Transactional(propagation = Propagation.REQUIRED)
    public Record getAccountSetting(String sysUserId) {
        Map<String, Object> attrs = new HashMap();
        attrs.put("sys_user_id", sysUserId);
        Record account = findOne(attrs);
        if (null != account && !account.isEmpty()) {
            return account;
        }
        String id = CommonUtils.getPrimaryKey();
        String token = CommonUtils.getPrimaryKey();
        attrs.put("id", id);
        attrs.put("in_time", new Date());
        attrs.put("token", token);
        attrs.put("url", AppConfig.DOMAIN_PAGE + "/wechat/api?ticket="
                + AesUtil.encrypt(id));
        attrs.put("valid_code", CommonUtils.getRandomNum(5));
        attrs.put("valid_state", VALID_STATE_NONACTIVATED);
        insert(attrs);
        return findById(id);
    }

    /**
     * 根据userId获得公众号信息
     *
     * @param sysUserId
     * @return
     */
    public Record getPublicAccountByUserid(String sysUserId) {
        StringBuilder sql = new StringBuilder(getSelectSql());
        sql.append(" where sys_user_id = ?");
        return findOne(sql.toString(), sysUserId);
    }

}