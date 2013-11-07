package sagan.blog.web;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.blog.service.BlogService;
import sagan.blog.view.PostView;
import sagan.blog.view.PostViewFactory;
import sagan.util.web.PageableFactory;
import sagan.util.web.PaginationInfo;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;

public class BlogController_PublishedPostsTests {

    private static final int TEST_PAGE = 1;

    @Mock
    private BlogService blogService;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Mock
    private PostViewFactory postViewFactory;

    private BlogController controller;
    private ExtendedModelMap model = new ExtendedModelMap();
    private List<PostView> posts = new ArrayList<>();
    private Page<PostView> page;
    private String viewName;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new BlogController(blogService, postViewFactory);

        List<Post> posts = new ArrayList<>();
        posts.add(PostBuilder.post().build());
        Page<Post> postsPage = new PageImpl<>(posts, new PageRequest(TEST_PAGE, 10), 20);
        Pageable testPageable = PageableFactory.forLists(TEST_PAGE);

        page = new PageImpl<>(new ArrayList<PostView>(), testPageable, 1);

        given(blogService.getPublishedPosts(eq(testPageable))).willReturn(postsPage);
        given(postViewFactory.createPostViewPage(postsPage)).willReturn(page);
        request.setServletPath("/blog");

        viewName = controller.listPublishedPosts(model, TEST_PAGE);
    }

    @Test
    public void providesAllCategoriesInModel() {
        assertThat((PostCategory[]) model.get("categories"), is(PostCategory.values()));
    }

    @Test
    public void providesPaginationInfoInModel() {
        assertThat((PaginationInfo) model.get("paginationInfo"), is(new PaginationInfo(page)));
    }

    @Test
    public void viewNameIsIndex() {
        assertThat(viewName, is("blog/index"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void postsInModel() {
        assertThat((List<PostView>) model.get("posts"), is(posts));
    }
}