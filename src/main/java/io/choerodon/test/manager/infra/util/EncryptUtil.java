package io.choerodon.test.manager.infra.util;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptProperties;
import org.hzero.starter.keyencrypt.core.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author superlee
 * @since 2020-07-15
 */
@Component
public class EncryptUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtil.class);

    public static final String BLANK_KEY = "";
    protected static final String[] IGNORE_VALUES = {"0","none"};
    private static ObjectMapper objectMapper = new ObjectMapper();

    private EncryptionService encryptionService;

    public EncryptUtil(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public void decryptIfFieldEncrypted(Field field, Object objectUpdate, Object v) throws IllegalAccessException {
        Encrypt encrypt = field.getAnnotation(Encrypt.class);
        if (encrypt != null && v instanceof String && EncryptContext.isEncrypt()) {
            String decrypt = encryptionService.decrypt(v.toString(), encrypt.value());
            field.set(objectUpdate, Long.parseLong(decrypt));
        } else {
            field.set(objectUpdate, v == null ? null : Long.valueOf(v.toString()));
        }
    }

    public void decryptSearchDTO(SearchDTO search) {
        if (!EncryptContext.isEncrypt()){
            return;
        }
        Optional<Map<String, Object>> adMapOptional = Optional.ofNullable(search).map(SearchDTO::getAdvancedSearchArgs);
        if (adMapOptional.isPresent()) {
            decryptAd(search, adMapOptional);
        }
        Optional<Map<String, Object>> searchArgs = Optional.ofNullable(search).map(SearchDTO::getSearchArgs);
        if (searchArgs.isPresent()) {
            decryptSa(search, searchArgs);
        }
        Optional<Map<String, Object>> otherArgs = Optional.ofNullable(search).map(SearchDTO::getOtherArgs);
        if (otherArgs.isPresent()) {
            decryptOa(search, otherArgs);
        }
    }

    private void decryptOa(SearchDTO search, Optional<Map<String, Object>> oaMapOptional) {
        List<String> temp;
        String tempStr;

        // priorityId
        temp = oaMapOptional.map(ad -> (List<String>) (ad.get("priorityId"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getOtherArgs().put("priorityId",
                    temp.stream().map(item -> encryptionService.decrypt(item, BLANK_KEY)).collect(Collectors.toList()));
        }

        // sprint
        temp = oaMapOptional.map(ad -> (List<String>) (ad.get("sprint"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getOtherArgs().put("sprint",
                    temp.stream().map(item -> Arrays.asList(IGNORE_VALUES).contains(item) ? item : encryptionService.decrypt(item, BLANK_KEY)).collect(Collectors.toList()));
        }

        // issueIds
        temp = oaMapOptional.map(ad -> (List<String>) (ad.get("issueIds"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getOtherArgs().put("issueIds",
                    temp.stream().map(item -> encryptionService.decrypt(item, BLANK_KEY)).collect(Collectors.toList()));
        }

        // assigneeId
        temp = oaMapOptional.map(ad -> (List<String>) (ad.get("assigneeId"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getOtherArgs().put("assigneeId",
                    temp.stream().map(item -> Arrays.asList(IGNORE_VALUES).contains(item) ? item : encryptionService.decrypt(item, BLANK_KEY)).collect(Collectors.toList()));
        }

        //userId
        decryptUserId(search, oaMapOptional);
    }

    private void decryptUserId(SearchDTO search, Optional<Map<String, Object>> oaMapOptional) {
        String tempStr = oaMapOptional.map(ad -> (String) ad.get("userId")).orElse(null);
        if (!Objects.isNull(tempStr)) {
            search.getOtherArgs().put("userId", Arrays.asList(IGNORE_VALUES).contains(tempStr) ? tempStr : encryptionService.decrypt(tempStr, BLANK_KEY));
        }
    }

    private void decryptAd(SearchDTO search, Optional<Map<String, Object>> adMapOptional) {
        List<String> temp;
        String tempStr;// versionList
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("versionList"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("versionList",
                    temp.stream().map(item -> Arrays.asList(IGNORE_VALUES).contains(item) ? item : Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }
        // statusList
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("statusList"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("statusList",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }
        // components
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("components"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("components",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }
        // sprints
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("sprints"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("sprints",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }
        // statusIdList
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("statusIdList"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("statusIdList",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }
        // prioritys
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("prioritys"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("prioritys",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }

        // issueTypeId
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("issueTypeId"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("issueTypeId",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }

        // statusId
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("statusId"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("statusId",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }

        // reporterIds
        temp = adMapOptional.map(ad -> (List<String>) (ad.get("reporterIds"))).orElse(null);
        if (CollectionUtils.isNotEmpty(temp)) {
            search.getAdvancedSearchArgs().put("reporterIds",
                    temp.stream().map(item -> Long.parseLong(encryptionService.decrypt(item, BLANK_KEY))).collect(Collectors.toList()));
        }

        // priorityId
        tempStr = adMapOptional.map(ad -> {
            if (Objects.isNull(ad.get("priorityId"))){
                return null;
            }else if (ad.get("priorityId") instanceof String){
                return (String) (ad.get("priorityId"));
            }else {
                try {
                    return objectMapper.writeValueAsString(ad.get("priorityId"));
                } catch (JsonProcessingException e) {
                    throw new CommonException(e);
                }
            }
        }).orElse(null);
        if (StringUtils.isNotBlank(tempStr)) {
            handlerPrimaryKey(tempStr, "priorityId", search.getAdvancedSearchArgs());
        }
    }

    private void handlerPrimaryKey(String tempStr, String key, Map<String, Object> map) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(tempStr);
            if (jsonNode.isArray()) {
                List list = objectMapper.readValue(tempStr, List.class);
                map.put(key, decryptList(list, BLANK_KEY, IGNORE_VALUES));
            } else {
                map.put(key, encryptionService.decrypt(tempStr, BLANK_KEY));
            }
        } catch (Exception e) {
            LOGGER.error("string to json node error: {}", e);
        }
    }

    /**
     * 解密List<String>形式的主键
     *
     * @param crypts
     * @param tableName
     * @return
     */
    private List<Long> decryptList(List<String> crypts, String tableName, String[] ignoreValue) {
        List<Long> cryptsLong = new ArrayList<>();
        List<String> ignoreValueList = new ArrayList<>();
        if (!ArrayUtils.isEmpty(ignoreValue)) {
            ignoreValueList.addAll(Arrays.asList(ignoreValue));
        }
        if (!CollectionUtils.isEmpty(crypts)) {
            for (String crypt : crypts) {
                if (!CollectionUtils.isEmpty(ignoreValueList) && ignoreValueList.contains(crypt)) {
                    cryptsLong.add(Long.valueOf(crypt));
                } else {
                    cryptsLong.add(decrypt(crypt, tableName));
                }
            }
        }
        return cryptsLong;
    }

    /**
     * 对单个主键进行解密
     *
     * @param crypt
     * @param tableName
     * @return
     */
    private Long decrypt(String crypt, String tableName) {
        if (StringUtils.isNumeric(crypt)) {
            return Long.parseLong(crypt);
        } else {
            return Long.parseLong(encryptionService.decrypt(crypt, tableName));
        }
    }

    @SuppressWarnings("unchecked")
    private void decryptSa(SearchDTO search, Optional<Map<String, Object>> saMapOptional) {
        String tempStr;
        // priorityId
        tempStr = saMapOptional.map(ad -> (String)(ad.get("priorityId"))).orElse(null);
        if (StringUtils.isNotBlank(tempStr)) {
            search.getSearchArgs().put("priorityId",encryptionService.decrypt(tempStr, BLANK_KEY));
        }
    }
}
