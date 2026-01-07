package it.younify.modules.sgtr.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CensorshipService {

    private volatile Trie trie;

    @Value("${app.censorship.bad-words:#{T(java.util.Collections).emptyList()}}")
    private List<String> badWords;

    @PostConstruct
    public void refresh() {

        Trie.TrieBuilder builder =
                Trie.builder()
                    .ignoreCase()
                    .onlyWholeWords();

        for (String word : badWords) {

            builder.addKeyword(word);
        }

        this.trie = builder.build();
    }


    public boolean containsSensitiveContent(String text) {

        if (text == null || text.isEmpty()) return false;

        return trie.containsMatch(text);
    }


    public String censorText(String text) {

        Collection<Emit> emits = trie.parseText(text);

        List<Emit> emitList = new ArrayList<>(emits);
        emitList.sort(Comparator.comparing(Emit::getStart).reversed());

        StringBuilder sb = new StringBuilder(text);

        for (Emit emit : emitList) {

            int start = emit.getStart();
            int end = emit.getEnd() + 1;

            String replacement = "SIG.RA ROSINA";
            sb.replace(start, end, replacement);
        }

        return sb.toString();
    }
}
