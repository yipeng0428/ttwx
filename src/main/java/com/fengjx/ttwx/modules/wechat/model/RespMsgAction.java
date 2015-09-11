
package com.fengjx.ttwx.modules.wechat.model;

import com.fengjx.ttwx.common.plugin.cache.IDataLoader;
import com.fengjx.ttwx.common.plugin.cache.ehcache.EhCacheUtil;
import com.fengjx.ttwx.common.plugin.db.Mapper;
import com.fengjx.ttwx.common.plugin.db.Model;
import com.fengjx.ttwx.common.plugin.db.Record;
import com.fengjx.ttwx.common.plugin.db.page.AdapterPage;
import com.fengjx.ttwx.common.system.exception.MyRuntimeException;
import com.fengjx.ttwx.common.utils.CommonUtils;
import com.fengjx.ttwx.modules.common.constants.AppConfig;
import me.chanjar.weixin.common.api.WxConsts;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息动作规则
 *
 * @author fengjx. @date：2015/6/5 0005
 */
@Mapper(table = "wechat_resp_msg_action")
@Component
public class RespMsgAction extends Model {

    private static final Logger LOG = LoggerFactory.getLogger(RespMsgAction.class);

    public static final String ACTION_TYPE_MATERIAL = "material";

    public static final String ACTION_TYPE_API = "api";

    public static final String FUZZY_EXACT = "1";

    public static final String FUZZY_CONTAIN = "2";

    public static final String FUZZY_START = "3";

    public static final String FUZZY_END = "4";

    /**
     * 保存消息响应规则
     *
     * @param actionMap
     * @param menuMap
     * @param materialMap
     */
    public void saveAction(Map<String, Object> actionMap, Map<String, Object> menuMap,
            Map<String, Object> materialMap) {
        // 菜单参数为空，表示不是菜单动作
        if (MapUtils.isNotEmpty(menuMap)) {
            saveMenuAction(actionMap, menuMap, materialMap);
        } else {
            saveMsgAction(actionMap, materialMap);
        }
    }

    /**
     * 更新消息响应规则
     *
     * @param actionMap
     * @param menuMap
     * @param materialMap
     */
    public void updateAction(Map<String, Object> actionMap, Map<String, Object> menuMap,
            Map<String, Object> materialMap) {
        String action_id = (String) actionMap.get("id");
        if (StringUtils.isNotBlank(action_id)) {
            deleteMsgActionById(action_id, (String) actionMap.get("user_id"));
        }
        saveAction(actionMap, menuMap, materialMap);
    }

    /**
     * 分页查询
     *
     * @params params 查询参数
     * @param userId 所属用户
     * @return
     */
    public AdapterPage pageMsgAction(Map<String, String> params, String userId) {
        List<Object> parameters = new ArrayList();
        StringBuilder sql = new StringBuilder(detailSql());
        sql.append(" where a.user_id = ? ");
        parameters.add(userId);
        if (StringUtils.isNotBlank(params.get("req_type"))) {
            sql.append(" and a.req_type = ?");
            parameters.add(params.get("req_type"));
        }
        if (StringUtils.isNotBlank(params.get("event_type"))) {
            sql.append(" and a.event_type = ?");
            parameters.add(params.get("event_type"));
        }
        if (StringUtils.isNotBlank(params.get("action_type"))) {
            sql.append(" and a.action_type = ?");
            parameters.add(params.get("action_type"));
        }
        if (StringUtils.isNotBlank(params.get("key_word"))) {
            sql.append(" and a.key_word like ?");
            parameters.add("%" + params.get("key_word") + "%");
        }
        if (StringUtils.isNotBlank(params.get("start_time"))) {
            sql.append(" and a.in_time >= ?");
            parameters.add(CommonUtils.string2Date(params.get("start_time").trim() + " 00:00:00"));
        }
        if (StringUtils.isNotBlank(params.get("end_time"))) {
            sql.append(" and a.in_time < ?");
            parameters.add(CommonUtils.string2Date(params.get("end_time").trim() + " 23:59:59"));
        }
        sql.append(" order by a.in_time desc");
        return paginate(sql.toString(), parameters.toArray()).convert();
    }

    /**
     * 根据ID读取规则详情
     *
     * @param id
     * @param userId
     * @return
     */
    public Record loadDetailById(String id, String userId) {
        StringBuilder sql = new StringBuilder(detailSql());
        sql.append(" where a.id = ? and a.user_id = ? ");
        return findOne(sql.toString(), id, userId);
    }

    /**
     * 查询唯一的消息响应规则
     *
     * @param ext_type
     * @param req_type
     * @param event_type
     * @param key_word
     * @param userId
     * @return
     */
    public Record loadMsgAction(String ext_type, String req_type, String event_type,
            String key_word, String userId) {
        return loadMsgAction(ext_type, req_type, event_type, key_word, null, userId);
    }

    /**
     * 查询唯一的消息响应规则
     *
     * @param ext_type 扩展的自定义类型（比如默认消息规则）
     * @param req_type 微信请求消息类型
     * @param event_type 事件类型
     * @param key_word 关键字/菜单key
     * @param fuzzy 模糊查询方式（只对关键字消息起作用）
     * @param userId
     * @return
     */
    public Record loadMsgAction(String ext_type, String req_type, String event_type,
            String key_word, String fuzzy, String userId) {
        List<Object> parameters = new ArrayList();
        StringBuilder sql = new StringBuilder(detailSql());
        sql.append(" where a.user_id = ? ");
        parameters.add(userId);
        if (StringUtils.isNotBlank(ext_type)) {
            sql.append(" and a.ext_type = ?");
            parameters.add(ext_type);
        }
        if (StringUtils.isNotBlank(req_type)) {
            sql.append(" and a.req_type = ?");
            parameters.add(req_type);
        }
        if (StringUtils.isNotBlank(event_type)) {
            sql.append(" and a.event_type = ?");
            parameters.add(event_type);
        }
        if (StringUtils.isNotBlank(key_word)) {
            sql.append(" and a.key_word = ?");
            parameters.add(key_word);
        }
        if (WxConsts.XML_MSG_TEXT.equals(req_type) && StringUtils.isNotBlank(fuzzy)) {
            sql.append(" and a.fuzzy = ?");
            parameters.add(fuzzy);
        }
        return findOne(sql.toString(), parameters.toArray());
    }

    /**
     * 查询关键字列表（关键字存在，模糊匹配的情况，所以可能有多条）
     *
     * @param key_word
     * @param userId
     * @return
     */
    public List<Map<String, Object>> loadKeywordActions(final String key_word,
            final String userId) {
        return EhCacheUtil.get(AppConfig.EhcacheName.WECHAT_KEYWORD_ACTION_CACHE, userId,
                new IDataLoader<List<Map<String, Object>>>() {
                    @Override
                    public List<Map<String, Object>> load() {
                        List<Object> parameters = new ArrayList();
                        StringBuilder sql = new StringBuilder(detailSql());
                        sql.append(" where a.user_id = ? and a.req_type = ? and a.key_word = ?");
                        parameters.add(userId);
                        parameters.add(WxConsts.XML_MSG_TEXT);
                        parameters.add(key_word);
                        return findList(sql.toString(), parameters.toArray());
                    }
                });
    }

    /**
     * 根据id批量删除消息规则
     *
     * @param ids
     * @param userId
     */
    public void deleteMsgActionById(String ids, final String userId) {
        if (StringUtils.isBlank(ids)) {
            throw new MyRuntimeException("ID为空，删除消息动作失败");
        }
        final String _ids[] = ids.split(",");
        for (String id : _ids) {
            removeCache(findById(id).getColumns());
        }
        final StringBuilder sql = new StringBuilder("delete from ");
        sql.append(getTableName());
        sql.append(" where user_id = ? and id = ?");
        batchExecute(sql.toString(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, userId);
                ps.setObject(2, _ids[i]);
            }

            @Override
            public int getBatchSize() {
                return _ids.length;
            }
        });
    }

    /**
     * 根据关键字删除规则
     *
     * @param key_word
     * @param userId
     */
    public void deleteMsgActionByKey(String key_word, String userId) {
        StringBuilder sql = new StringBuilder("delete from ");
        sql.append(getTableName()).append(" where key_word = ? and user_id = ?");
        execute(sql.toString(), key_word, userId);
    }

    /**
     * 保存菜单动作
     *
     * @param actionMap
     * @param menuMap
     * @param materialMap
     */
    private void saveMenuAction(Map<String, Object> actionMap, Map<String, Object> menuMap,
            Map<String, Object> materialMap) {
        Date now = new Date();
        actionMap.put("in_time", now);
        String menuId = (String) menuMap.get("id");
        menuMap.put("update_time", now);
        String menuType = (String) menuMap.get("type");
        // 菜单类型为click
        if (menuType.equals(WxConsts.BUTTON_CLICK)) {
            menuMap.put("menu_key", "key_" + menuId);
            menuMap.put("url", "");
            // 请求关键字 or 菜单点击key
            actionMap.put("key_word", "key_" + menuId);
            // 消息响应类型
            String action_type = (String) actionMap.get("action_type");
            if (ACTION_TYPE_MATERIAL.equals(action_type)) { // 从素材读取数据
                // 消息响应类型
                String resp_type = (String) materialMap.get("msg_type");
                // 消息回复类型是文字，则先将文字信息保存到素材表
                if (WxConsts.XML_MSG_TEXT.equals(resp_type)) {
                    materialMap.put("id", CommonUtils.getPrimaryKey());
                    materialMap.put("in_time", now);
                    insert(Material.class, materialMap);
                }
            }
            actionMap.put("material_id", materialMap.get("id"));
            insert(actionMap);
        } else if (menuType.equals(WxConsts.BUTTON_VIEW)) {
            menuMap.put("menu_key", null);
        }
        update(WechatMenu.class, menuMap);
    }

    /**
     * 保存其他（除菜单外）消息动作
     *
     * @param actionMap
     * @param materialMap
     */
    private void saveMsgAction(Map<String, Object> actionMap, Map<String, Object> materialMap) {
        // 保存前先判断改消息规则是否存在，某种规则必须确保唯一
        if (isExist(actionMap)) {
            throw new MyRuntimeException("相同的消息规则已经存在");
        }
        Date now = new Date();
        actionMap.put("in_time", now);
        // 消息响应类型
        String action_type = (String) actionMap.get("action_type");
        if (ACTION_TYPE_MATERIAL.equals(action_type)) { // 从素材读取数据
            // 消息响应类型
            String resp_type = (String) materialMap.get("msg_type");
            // 消息回复类型是文字，则先将文字信息保存到素材表
            if (WxConsts.XML_MSG_TEXT.equals(resp_type)) {
                materialMap.put("id", CommonUtils.getPrimaryKey());
                materialMap.put("in_time", now);
                insert(Material.class, materialMap);
                actionMap.put("material_id", materialMap.get("id"));
            }
        }
        doSave(actionMap);
    }

    private void doSave(Map<String, Object> actionMap) {
        insert(actionMap);
        removeCache(actionMap);
    }

    /**
     * 判断消息规则是否存在
     *
     * @param attrs
     * @return
     */
    private boolean isExist(Map<String, Object> attrs) {
        Record record = loadMsgAction((String) attrs.get("ext_type"),
                (String) attrs.get("req_type"), (String) attrs.get("event_type"),
                (String) attrs.get("key_word"), (String) attrs.get("fuzzy"),
                (String) attrs.get("user_id"));
        return !record.isEmpty();
    }

    /**
     * 规则详情查询sql
     * 
     * @return
     */
    private String detailSql() {
        StringBuffer sql = new StringBuffer(
                "select a.id as id, a.req_type as req_type, a.action_type as action_type, a.key_word as key_word, a.fuzzy as fuzzy, a.material_id as material_id, a.app_id as app_id, a.in_time as in_time,");
        sql.append(" b.bean_name as bean_name, b.name as app_name,");
        sql.append(" c.xml_data as xml_data, c.msg_type as msg_type");
        sql.append(" from wechat_resp_msg_action a");
        sql.append(" left join wechat_ext_app b ");
        sql.append(" on a.app_id = b.id");
        sql.append(" left join wechat_material c");
        sql.append(" on a.material_id = c.id");
        return sql.toString();
    }

    /**
     * 删除action缓存
     *
     * @param actionMap
     */
    private void removeCache(Map<String, Object> actionMap) {
        if (MapUtils.isEmpty(actionMap)) {
            return;
        }
        String extType = (String) actionMap.get("ext_type");
        String reqType = (String) actionMap.get("req_type");
        String eventType = (String) actionMap.get("event_type");
        String keyword = (String) actionMap.get("key_word");
        String userId = (String) actionMap.get("user_id");
        if (WxConsts.XML_MSG_TEXT.equals(reqType)) {
            EhCacheUtil.remove(AppConfig.EhcacheName.WECHAT_KEYWORD_ACTION_CACHE, userId);
        } else {
            EhCacheUtil.remove(AppConfig.EhcacheName.WECHAT_ACTION_CACHE,
                    buildCacheKey(extType, reqType, eventType, keyword, userId));
        }
    }

    /**
     * 构建缓存key
     *
     * @param extType
     * @param reqType
     * @param eventType
     * @param keyword
     * @param userId
     * @return
     */
    private String buildCacheKey(String extType, String reqType, String eventType, String keyword,
            String userId) {
        String spit = "_";
        String nullStr = "XXX";
        StringBuilder res = new StringBuilder();
        res.append(StringUtils.isBlank(extType) ? nullStr : extType).append(spit);
        res.append(StringUtils.isBlank(reqType) ? nullStr : reqType).append(spit);
        res.append(StringUtils.isBlank(eventType) ? nullStr : eventType).append(spit);
        res.append(StringUtils.isBlank(keyword) ? nullStr : keyword).append(spit);
        res.append(StringUtils.isBlank(userId) ? nullStr : userId).append(spit);
        return res.toString();
    }

}
