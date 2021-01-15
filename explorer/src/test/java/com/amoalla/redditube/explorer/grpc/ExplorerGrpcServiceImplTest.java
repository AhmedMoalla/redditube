package com.amoalla.redditube.explorer.grpc;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.client.model.deserializer.MediaPostDtoDeserializer;
import com.amoalla.redditube.commons.grpc.mapping.ProtoMappingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Collections;

import static com.amoalla.redditube.commons.api.Explorer.*;
import static org.mockito.Mockito.*;

class ExplorerGrpcServiceImplTest {

    public static final String TEST_HANDLE = "TEST_HANDLE";
    public static final String TEST_AFTER = "TEST_AFTER";
    public static final String TEST_BEFORE = "TEST_BEFORE";
    public static final int TEST_LIMIT = 10;

    private ExplorerGrpcServiceImpl explorerGrpcService;
    private MediaPostDto testDto;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        ExplorerService explorerService = mock(ExplorerService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("MediaPostDtoDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(MediaPostDto.class, new MediaPostDtoDeserializer());
        objectMapper.registerModule(module);
        testDto = objectMapper.readValue(MEDIA_POST_JSON, MediaPostDto.class);
        when(explorerService.getPosts(TEST_HANDLE, TEST_AFTER, TEST_BEFORE, TEST_LIMIT, Sort.NEW))
                .thenReturn(Flux.just(testDto));
        explorerGrpcService = new ExplorerGrpcServiceImpl(explorerService);
    }

    @Test
    void testGetMediaPosts() {
        GetMediaPostsRequest request = GetMediaPostsRequest.newBuilder()
                .setUsernameOrSubreddit(TEST_HANDLE)
                .setAfter(TEST_AFTER)
                .setBefore(TEST_BEFORE)
                .setLimit(TEST_LIMIT)
                .build();
        StreamObserver<GetMediaPostsResponse> observer = mock(StreamObserver.class);
        explorerGrpcService.getMediaPosts(request, observer);

        MediaPost expected = ProtoMappingUtils.mapToProto(testDto);
        GetMediaPostsResponse expectedResponse = GetMediaPostsResponse.newBuilder()
                .addAllMediaPosts(Collections.singletonList(expected))
                .build();
        verify(observer).onNext(expectedResponse);
        verify(observer).onCompleted();
    }

    private final static String MEDIA_POST_JSON = "{\n" +
            "          \"approved_at_utc\": null,\n" +
            "          \"subreddit\": \"pics\",\n" +
            "          \"selftext\": \"\",\n" +
            "          \"author_fullname\": \"t2_5b9ygky5\",\n" +
            "          \"saved\": false,\n" +
            "          \"mod_reason_title\": null,\n" +
            "          \"gilded\": 0,\n" +
            "          \"clicked\": false,\n" +
            "          \"title\": \"Moonrise ( AWD, Netherlands) [OC] [4000 x 6000]\",\n" +
            "          \"link_flair_richtext\": [],\n" +
            "          \"subreddit_name_prefixed\": \"r/pics\",\n" +
            "          \"hidden\": false,\n" +
            "          \"pwls\": 6,\n" +
            "          \"link_flair_css_class\": null,\n" +
            "          \"downs\": 0,\n" +
            "          \"thumbnail_height\": 140,\n" +
            "          \"top_awarded_type\": null,\n" +
            "          \"hide_score\": false,\n" +
            "          \"name\": \"t3_g0gsoq\",\n" +
            "          \"quarantine\": false,\n" +
            "          \"link_flair_text_color\": \"dark\",\n" +
            "          \"upvote_ratio\": 0.99,\n" +
            "          \"author_flair_background_color\": null,\n" +
            "          \"subreddit_type\": \"public\",\n" +
            "          \"ups\": 36,\n" +
            "          \"total_awards_received\": 0,\n" +
            "          \"media_embed\": {},\n" +
            "          \"thumbnail_width\": 140,\n" +
            "          \"author_flair_template_id\": null,\n" +
            "          \"is_original_content\": false,\n" +
            "          \"user_reports\": [],\n" +
            "          \"secure_media\": null,\n" +
            "          \"is_reddit_media_domain\": true,\n" +
            "          \"is_meta\": false,\n" +
            "          \"category\": null,\n" +
            "          \"secure_media_embed\": {},\n" +
            "          \"link_flair_text\": null,\n" +
            "          \"can_mod_post\": false,\n" +
            "          \"score\": 36,\n" +
            "          \"approved_by\": null,\n" +
            "          \"author_premium\": false,\n" +
            "          \"thumbnail\": \"https://b.thumbs.redditmedia.com/g7NPz5WiVBYExjjw8idcKCbCMjthb79j2CYpjohAk8Q.jpg\",\n" +
            "          \"edited\": false,\n" +
            "          \"author_flair_css_class\": null,\n" +
            "          \"author_flair_richtext\": [],\n" +
            "          \"gildings\": {},\n" +
            "          \"post_hint\": \"image\",\n" +
            "          \"content_categories\": [\n" +
            "            \"photography\"\n" +
            "          ],\n" +
            "          \"is_self\": false,\n" +
            "          \"mod_note\": null,\n" +
            "          \"created\": 1586804393.0,\n" +
            "          \"link_flair_type\": \"text\",\n" +
            "          \"wls\": 6,\n" +
            "          \"removed_by_category\": null,\n" +
            "          \"banned_by\": null,\n" +
            "          \"author_flair_type\": \"text\",\n" +
            "          \"domain\": \"i.redd.it\",\n" +
            "          \"allow_live_comments\": false,\n" +
            "          \"selftext_html\": null,\n" +
            "          \"likes\": null,\n" +
            "          \"suggested_sort\": null,\n" +
            "          \"banned_at_utc\": null,\n" +
            "          \"url_overridden_by_dest\": \"https://i.redd.it/gyalykyqgks41.jpg\",\n" +
            "          \"view_count\": null,\n" +
            "          \"archived\": true,\n" +
            "          \"no_follow\": false,\n" +
            "          \"is_crosspostable\": true,\n" +
            "          \"pinned\": false,\n" +
            "          \"over_18\": false,\n" +
            "          \"preview\": {\n" +
            "            \"images\": [\n" +
            "              {\n" +
            "                \"source\": {\n" +
            "                  \"url\": \"https://preview.redd.it/gyalykyqgks41.jpg?auto=webp&amp;s=cc7c94bfdf2749ce52125f02ae901ca0ca898ca9\",\n" +
            "                  \"width\": 4000,\n" +
            "                  \"height\": 6000\n" +
            "                },\n" +
            "                \"resolutions\": [\n" +
            "                  {\n" +
            "                    \"url\": \"https://preview.redd.it/gyalykyqgks41.jpg?width=108&amp;crop=smart&amp;auto=webp&amp;s=865e6e85a6abd2d530e6873a77db4e40fe06b5e2\",\n" +
            "                    \"width\": 108,\n" +
            "                    \"height\": 162\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"url\": \"https://preview.redd.it/gyalykyqgks41.jpg?width=216&amp;crop=smart&amp;auto=webp&amp;s=95506027e373989b44aeb1820f6fc0580f610dc6\",\n" +
            "                    \"width\": 216,\n" +
            "                    \"height\": 324\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"url\": \"https://preview.redd.it/gyalykyqgks41.jpg?width=320&amp;crop=smart&amp;auto=webp&amp;s=0e0dd8c656c4226b39f35f3cbaf1058b16854212\",\n" +
            "                    \"width\": 320,\n" +
            "                    \"height\": 480\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"url\": \"https://preview.redd.it/gyalykyqgks41.jpg?width=640&amp;crop=smart&amp;auto=webp&amp;s=ebb93e0fa653dddbfefb9527cfd1a61ebdfb1991\",\n" +
            "                    \"width\": 640,\n" +
            "                    \"height\": 960\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"url\": \"https://preview.redd.it/gyalykyqgks41.jpg?width=960&amp;crop=smart&amp;auto=webp&amp;s=cba52a6d564bc303221609a59828b06311044bcf\",\n" +
            "                    \"width\": 960,\n" +
            "                    \"height\": 1440\n" +
            "                  },\n" +
            "                  {\n" +
            "                    \"url\": \"https://preview.redd.it/gyalykyqgks41.jpg?width=1080&amp;crop=smart&amp;auto=webp&amp;s=b4e3fd63996fbd37f30209572ab2bb8a8378b627\",\n" +
            "                    \"width\": 1080,\n" +
            "                    \"height\": 1620\n" +
            "                  }\n" +
            "                ],\n" +
            "                \"variants\": {},\n" +
            "                \"id\": \"OPH0lKwL7C5puWFY8yXO8UYDPbL3tPPU1GnVRIp0ne0\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"enabled\": true\n" +
            "          },\n" +
            "          \"all_awardings\": [],\n" +
            "          \"awarders\": [],\n" +
            "          \"media_only\": false,\n" +
            "          \"can_gild\": true,\n" +
            "          \"spoiler\": false,\n" +
            "          \"locked\": false,\n" +
            "          \"author_flair_text\": null,\n" +
            "          \"treatment_tags\": [],\n" +
            "          \"visited\": false,\n" +
            "          \"removed_by\": null,\n" +
            "          \"num_reports\": null,\n" +
            "          \"distinguished\": null,\n" +
            "          \"subreddit_id\": \"t5_2qh0u\",\n" +
            "          \"mod_reason_by\": null,\n" +
            "          \"removal_reason\": null,\n" +
            "          \"link_flair_background_color\": \"\",\n" +
            "          \"id\": \"g0gsoq\",\n" +
            "          \"is_robot_indexable\": true,\n" +
            "          \"report_reasons\": null,\n" +
            "          \"author\": \"_Orionids_\",\n" +
            "          \"discussion_type\": null,\n" +
            "          \"num_comments\": 1,\n" +
            "          \"send_replies\": true,\n" +
            "          \"whitelist_status\": \"all_ads\",\n" +
            "          \"contest_mode\": false,\n" +
            "          \"mod_reports\": [],\n" +
            "          \"author_patreon_flair\": false,\n" +
            "          \"author_flair_text_color\": null,\n" +
            "          \"permalink\": \"/r/pics/comments/g0gsoq/moonrise_awd_netherlands_oc_4000_x_6000/\",\n" +
            "          \"parent_whitelist_status\": \"all_ads\",\n" +
            "          \"stickied\": false,\n" +
            "          \"url\": \"https://i.redd.it/gyalykyqgks41.jpg\",\n" +
            "          \"subreddit_subscribers\": 26531427,\n" +
            "          \"created_utc\": 1586775593.0,\n" +
            "          \"num_crossposts\": 0,\n" +
            "          \"media\": null,\n" +
            "          \"is_video\": false\n" +
            "        }";
}