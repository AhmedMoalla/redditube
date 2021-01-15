package com.amoalla.redditube.mediaposts.listener.resolver.impl;

import com.amoalla.redditube.mediaposts.listener.resolver.EmbedUrlResolver;
import com.amoalla.redditube.mediaposts.listener.resolver.ResolverMediaUrls;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RedGifsEmbedUrlResolver implements EmbedUrlResolver {

    private static final String PROVIDER_NAME = "RedGIFS";

    @Override
    public ResolverMediaUrls resolve(String embedHtml) {
        try {
            Element iframe = Jsoup.parseBodyFragment(embedHtml).body().selectFirst("iframe");
            String gifUrl = iframe.attr("src");
            Document gif = Jsoup.connect(gifUrl).get();
            Element video = gif.body().selectFirst("video");
            String thumbnailUrl = video.attr("poster");
            String mediaUrl = video.selectFirst("source[type='video/mp4']:not([src*=mobile])").attr("src");
            return new ResolverMediaUrls(mediaUrl, thumbnailUrl);
        } catch (IOException e) {
            log.error("An error happened while trying to resolve RedGIFS embed html: {}", embedHtml, e);
            return null;
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
