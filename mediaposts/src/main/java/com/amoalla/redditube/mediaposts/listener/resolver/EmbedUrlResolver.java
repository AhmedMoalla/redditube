package com.amoalla.redditube.mediaposts.listener.resolver;

/**
 * Extracts a media post url from embedded html posts
 */
public interface EmbedUrlResolver {
    ResolverMediaUrls resolve(String embedHtml);
    String getProviderName();
}
