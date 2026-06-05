package com.seekweb4.chat.modules.sys.service;

import com.seekweb4.chat.common.utils.StringUtils;
import com.seekweb4.chat.core.service.CrudService;
import com.seekweb4.chat.modules.sys.entity.SensitiveWord;
import com.seekweb4.chat.modules.sys.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class SensitiveWordService extends CrudService<SensitiveWordMapper, SensitiveWord> {

    public List<String> findWords() {
        List<SensitiveWord> rows = super.findList(new SensitiveWord());
        List<String> words = new ArrayList<>();
        if (rows == null || rows.isEmpty()) {
            return words;
        }
        for (SensitiveWord row : rows) {
            if (row != null && StringUtils.isNotBlank(row.getWord())) {
                words.add(row.getWord());
            }
        }
        return words;
    }

    @Transactional(readOnly = false)
    public void saveWords(List<String> words) {
        if (words == null || words.isEmpty()) {
            return;
        }
        Set<String> unique = normalize(words);
        for (String word : unique) {
            SensitiveWord existing = super.findUniqueByProperty("word", word);
            if (existing == null) {
                SensitiveWord entity = new SensitiveWord();
                entity.setWord(word);
                super.save(entity);
            }
        }
    }

    @Transactional(readOnly = false)
    public void removeWords(List<String> words) {
        if (words == null || words.isEmpty()) {
            return;
        }
        Set<String> unique = normalize(words);
        for (String word : unique) {
            SensitiveWord existing = super.findUniqueByProperty("word", word);
            if (existing != null) {
                super.delete(existing);
            }
        }
    }

    @Transactional(readOnly = false)
    public void replaceAll(List<String> words) {
        List<SensitiveWord> all = super.findList(new SensitiveWord());
        if (all != null && !all.isEmpty()) {
            super.deleteAll(all);
        }
        saveWords(words);
    }

    private Set<String> normalize(List<String> words) {
        Set<String> unique = new LinkedHashSet<>();
        for (String word : words) {
            if (StringUtils.isNotBlank(word)) {
                unique.add(word.trim());
            }
        }
        return unique;
    }
}
