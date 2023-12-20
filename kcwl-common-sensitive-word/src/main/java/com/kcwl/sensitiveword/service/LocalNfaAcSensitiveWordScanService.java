package com.kcwl.sensitiveword.service;

import com.kcwl.sensitiveword.datasource.DataSourceAdapter;
import com.kcwl.sensitiveword.enums.SensitiveLevelEnum;
import com.kcwl.sensitiveword.enums.SensitiveWordTypeEnum;
import com.kcwl.sensitiveword.pojo.dto.FoundWordInfoDto;
import com.kcwl.sensitiveword.pojo.po.SensitiveWordInfoSlim;

import java.util.*;

/**
 * <p>
 * 敏感词 搜索 service
 * </p>
 *
 * @author renyp
 * @since 2023/5/29 11:04
 */

public class LocalNfaAcSensitiveWordScanService extends SensitiveWordScanService {

    /**
     * AC树的根节点
     */
    private final Node root;

    public LocalNfaAcSensitiveWordScanService(DataSourceAdapter dataSourceAdapter) {
        super(dataSourceAdapter);
        this.root = new Node();
    }


    @Override
    public boolean initializeWordTree() {
        this.loadAllSensitiveWord().forEach(this::insert);
        this.buildAc();
        return true;
    }

    @Override
    public boolean existsSensitiveWord(String text, SensitiveLevelEnum sensitiveWordLevel) {
        Node pointer = root, k = null;
        for (int i = 0, len = text.length(); i < len; i++) {
            int ind = text.charAt(i);
            // 状态转移(沿着fail指针链接的链表，此处区别于DFA模型)
            while (pointer != null && pointer.next.get(ind) == null) {
                pointer = pointer.fail;
            }
            if (pointer == null) {
                pointer = root;
            } else {
                pointer = pointer.next.get(ind);
            }
            // 提取结果(沿着fail指针链接的链表，此处区别于DFA模型)
            k = pointer;
            while (k != null) {
                // 处理 不同 敏感级别
                if (k.flag && k.sensitiveLevelMark >= sensitiveWordLevel.getBitShift()) {
                    return true;
                }
                k = k.fail;
            }
        }
        return false;
    }

    @Override
    public FoundWordInfoDto searchSensitiveWord(String text, boolean isDensityMatch) {
        Node pointer = root, k = null;
        for (int i = 0, len = text.length(); i < len; i++) {
            int ind = text.charAt(i);
            // 状态转移(沿着fail指针链接的链表，此处区别于DFA模型)
            while (pointer != null && pointer.next.get(ind) == null) {
                pointer = pointer.fail;
            }
            if (pointer == null) {
                pointer = root;
            } else {
                pointer = pointer.next.get(ind);
            }
            // 提取结果(沿着fail指针链接的链表，此处区别于DFA模型)
            k = pointer;
            while (k != null) {
                if (k.flag) {
                    // TODO: 2023/5/31 敏感词 类型、级别信息填充
                    return new FoundWordInfoDto(k.str, i - k.str.length() + 1, i, SensitiveLevelEnum.NORMAL, SensitiveWordTypeEnum.ALL);
                }
                k = k.fail;
            }
        }
        return new FoundWordInfoDto(null, -1, -1, null, null);
    }

    @Override
    public List<FoundWordInfoDto> searchAllSensitiveWord(String text, boolean isDensityMatch) {
        List<FoundWordInfoDto> ans = new ArrayList<>();
        Node pointer = root, k = null;
        for (int i = 0, len = text.length(); i < len; i++) {
            int ind = text.charAt(i);
            // 状态转移(沿着fail指针链接的链表，此处区别于DFA模型)
            while (pointer != null && pointer.next.get(ind) == null) {
                pointer = pointer.fail;
            }
            if (pointer == null) {
                pointer = root;
            } else {
                pointer = pointer.next.get(ind);
            }
            // 提取结果(沿着fail指针链接的链表，此处区别于DFA模型)
            k = pointer;
            while (k != null) {
                if (k.flag) {
                    ans.add(new FoundWordInfoDto(k.str, i - k.str.length() + 1, i, SensitiveLevelEnum.NORMAL, SensitiveWordTypeEnum.ALL));
                    if (!isDensityMatch) {
                        pointer = root;
                        break;
                    }
                }
                k = k.fail;
            }
        }
        return ans;
    }

    /**
     * 词库添加新词，初始化查找树
     *
     * @param sensitiveWordInfoSlim 添加的新词
     */
    public void insert(SensitiveWordInfoSlim sensitiveWordInfoSlim) {
        Node pointer = root;
        for (char curr : sensitiveWordInfoSlim.getSensitiveWord().toCharArray()) {
            if (pointer.next.get((int) curr) == null) {
                pointer.next.put((int) curr, new Node());
            }
            pointer = pointer.next.get((int) curr);
        }
        pointer.flag = true;
        pointer.str = sensitiveWordInfoSlim.getSensitiveWord();
        pointer.sensitiveLevelMark |= SensitiveLevelEnum.exchangeByLevelValue(sensitiveWordInfoSlim.getSensitiveLevel()).orElse(SensitiveLevelEnum.STRICT).getBitShift();
    }

    /**
     * 构建基于NFA模型的 AC自动机
     */
    public void buildAc() {
        Queue<Node> queue = new LinkedList<>();
        Node pointer = root;
        for (Integer key : pointer.next.keySet()) {
            pointer.next.get(key).fail = root;
            queue.offer(pointer.next.get(key));
        }
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            for (Integer key : curr.next.keySet()) {
                Node fail = curr.fail;
                // 查找当前节点匹配失败，他对应等效匹配的节点是哪个
                while (fail != null && fail.next.get(key) == null) {
                    fail = fail.fail;
                }
                // 代码到这，有两种可能，fail不为null，说明找到了fail；fail为null，没有找到，那么就把fail指向root节点（当到该节点匹配失败，那么从root节点开始重新匹配）
                if (fail != null) {
                    fail = fail.next.get(key);
                } else {
                    fail = root;
                }
                curr.next.get(key).fail = fail;
                queue.offer(curr.next.get(key));
            }
        }
    }


    /**
     * 字典树节点
     */
    private static class Node {

        /**
         * 当前节点是否是一个单词的结尾
         */
        boolean flag;
        /**
         * 指向 当前节点匹配失败应该跳转的下个节点
         */
        Node fail;
        /**
         * 以当前节点结尾的单词
         */
        String str;
        /**
         * 以当前节点结尾的单词 所属于的敏感级别
         */
        int sensitiveLevelMark;

        /**
         * 当前节点的子节点
         */
        Map<Integer, Node> next;

        public Node() {
            this.flag = false;
            next = new HashMap<>();
        }
    }

}
