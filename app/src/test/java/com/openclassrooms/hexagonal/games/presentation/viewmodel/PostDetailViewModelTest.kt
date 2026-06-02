package com.openclassrooms.hexagonal.games.presentation.viewmodel

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.domain.usecase.AddCommentUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.DeletePostUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.GetPostByIdUseCase
import com.openclassrooms.hexagonal.games.domain.usecase.ManageUserUseCase
import com.openclassrooms.hexagonal.games.domain.util.AppState
import com.openclassrooms.hexagonal.games.presentation.screen.homefeed.PostUi
import com.openclassrooms.hexagonal.games.presentation.screen.postdetail.PostDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getPostByIdUseCase: GetPostByIdUseCase
    private lateinit var addCommentUseCase: AddCommentUseCase
    private lateinit var deletePostUseCase: DeletePostUseCase
    private lateinit var manageUserUseCase: ManageUserUseCase
    private lateinit var viewModel: PostDetailViewModel

    @Before
    fun setUp() {
        getPostByIdUseCase = mockk()
        addCommentUseCase = mockk(relaxed = true)
        deletePostUseCase = mockk(relaxed = true)
        manageUserUseCase = mockk()

        viewModel = PostDetailViewModel(
            getPostByIdUseCase,
            addCommentUseCase,
            deletePostUseCase,
            manageUserUseCase
        )
    }

    @Test
    fun `fetchPost should update post, appState and currentUserId when user is logged in`() =
        runTest {
            // Given
            val postId = "post123"
            val author = User(id = "u1", nameUser = "Alice Dupont")
            val domainPost = Post(
                id = postId,
                title = "Mon post",
                description = "Une description",
                photoUrl = null,
                timestamp = 0,
                author = author
            )
            val firebaseUser = mockk<FirebaseUser>()
            val userId = "user123"

            coEvery { getPostByIdUseCase(postId) } returns domainPost
            every { manageUserUseCase.getUser() } returns flowOf(firebaseUser)
            every { firebaseUser.uid } returns userId

            // When
            viewModel.fetchPost(postId)

            // Then — on vérifie le PostUi mappé, pas le Post domain
            val expectedPostUi = PostUi(
                id = postId,
                authorId = "u1",
                authorName = "Alice Dupont",
                title = "Mon post",
                description = "Une description",
                photoUrl = null
            )
            Assert.assertEquals(expectedPostUi, viewModel.post.value)
            Assert.assertEquals(AppState.Ready, viewModel.appState.value)
            Assert.assertEquals(userId, viewModel.uiState.value.currentUserId)
        }

    @Test
    fun `addComment should call usecase`() = runTest {
        // Given
        val postId = "post123"
        val content = "Nice post"

        // When
        viewModel.addComment(postId, content)

        // Then
        coVerify { addCommentUseCase(postId, content) }
    }

    @Test
    fun `deletePost should update success state when successful`() = runTest {
        // Given
        val postId = "post123"
        coEvery { deletePostUseCase(postId) } returns Unit

        // When
        viewModel.deletePost(postId)

        // Then
        coVerify { deletePostUseCase(postId) }
        Assert.assertTrue(viewModel.uiState.value.deleteSuccess)
        Assert.assertNull(viewModel.uiState.value.deleteError)
    }

    @Test
    fun `deletePost should update error state when usecase fails`() = runTest {
        // Given
        val postId = "post123"
        val errorMessage = "Error deleting post"
        coEvery { deletePostUseCase(postId) } throws Exception(errorMessage)

        // When
        viewModel.deletePost(postId)

        // Then
        Assert.assertEquals(errorMessage, viewModel.uiState.value.deleteError)
        Assert.assertTrue(!viewModel.uiState.value.deleteSuccess)
    }
}