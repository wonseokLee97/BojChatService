package com.prod.realtimechat.chat.application;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class XssSanitizer {

    public String sanitize(String input) {
        Safelist safelist = Safelist.basic(); // 기본적인 Safelist

        safelist.addTags("img", "video", "source", "figcaption", "figure");
        safelist.addAttributes("img", "src", "alt", "width", "height");
        safelist.addAttributes("video", "src", "controls", "width", "height");

        // HTML을 정화하여 반환
        return Jsoup.clean(input, safelist);
    }
}