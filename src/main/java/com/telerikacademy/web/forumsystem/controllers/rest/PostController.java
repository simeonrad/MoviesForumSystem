package com.telerikacademy.web.forumsystem.controllers.rest;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.NotAllowedContentException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.*;
import com.telerikacademy.web.forumsystem.models.*;
import com.telerikacademy.web.forumsystem.services.CommentService;
import com.telerikacademy.web.forumsystem.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;


@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final TagHelper tagHelper;
    private final PostMapper postMapper;
    private final TextPurifier textPurifier;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper,
                          TagHelper tagHelper, TextPurifier textPurifier, CommentService commentService, CommentMapper commentMapper) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.tagHelper = tagHelper;
        this.textPurifier = textPurifier;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @PostMapping()
    @Operation(
            summary = "Creating a post",
            description = "This method is used for creating a post. Ensure the title and content adhere to the specified length requirements.",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = PostView.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include title (min = 16, max = 64 characters) and content (min = 32, max = 8192 characters) in the request body."
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of a post", content = @Content(schema = @Schema(implementation = PostDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password.")
            }
    )
    public PostDto createPost(@RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Post post = postMapper.fromDto(postDto);
            postService.create(post, currentUser);
            return postMapper.toDto(post);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        }
    }

    @PutMapping("/post/{postId}")
    @Operation(
            summary = "Update a post",
            description = "This method is used for updating a post. If the user from headers is not the author of the post, access will be denied.",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = PostView.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include title (min = 16, max = 64) and content (min = 32, max = 8192) in the request body."
            ),
            parameters = {
                    @Parameter(name = "postId", description = "Path variable that is the ID of the post you are trying to edit.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful updating of a post", content = @Content(schema = @Schema(implementation = CommentDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password, or the user is not the author of the post."),
                    @ApiResponse(responseCode = "404", description = "No such post found.")
            }
    )
    public PostDto updatePost(@PathVariable int postId, @RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Set<Tag> tags = tagHelper.tryGetTags(headers);
            Post post = postService.getById(postId);
            postMapper.updateFromDto(postDto, post);
            postService.update(post, currentUser, tags);
            return postMapper.toDto(post);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    @Operation(
            summary = "Deleting a post",
            description = "This method is used for deleting a post. The operation requires that the requester is either the author of the post or an admin.",
            parameters = {
                    @Parameter(name = "postId", description = "The ID of the post you are trying to delete.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of a post"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password, or the requester is neither the author of the post nor an admin."),
                    @ApiResponse(responseCode = "404", description = "Post not found - no such post found.")
            }
    )
    public void deletePost(@PathVariable int postId, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            Post post = postService.getById(postId);
            postService.delete(post, currentUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/post/{postId}")
    @Operation(
            summary = "Commenting on a post",
            description = "This method is used for creating and adding a comment to a certain post. Ensure the comment does not contain forbidden content.",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = CommentDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body (min = 1, max = 500 characters)."
            ),
            parameters = {
                    @Parameter(name = "postId", description = "The ID of the post you are trying to comment on.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful comment creation and adding it to a post", content = @Content(schema = @Schema(implementation = CommentDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "Post not found - there is no such post."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the content in the comment contains forbidden content.")
            }
    )
    public CommentDto commentOnPost(@RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto, @PathVariable int postId) {
        try {
            User author = authenticationHelper.tryGetUser(headers);
            Comment comment = commentMapper.fromDto(commentDto, author, postId);
            textPurifier.checkTextAndBan(comment.getContent(), author);
            commentService.create(comment, author);
            return commentMapper.toDto(comment);
        } catch (NotAllowedContentException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/api/post/{postId}")
    @Operation(
            summary = "Get comments to a post",
            description = "This method is used for getting all comments to a certain post",
            parameters = {@Parameter(name = "postId", description = "This is the Id of the post you are trying get the comments of.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successful got some comments to a existing post"),
                    @ApiResponse(responseCode = "404",
                            description = "There is no such post or there aren't any comments on it.")}
    )
    public List<CommentDto> getCommentsOnPost(@PathVariable int postId) {
        try {
            return commentService.getByPostId(postId)
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{postId}/like")
    @Operation(
            summary = "Liking a post",
            description = "This method is used for liking a certain post. If you use the method after you have already liked it, you will unlike the certain post.",
            parameters = {
                    @Parameter(name = "postId", description = "The ID of the post you are trying to like.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully liked or unliked post"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "Post not found - there is no such post.")
            }
    )
    public void likePost(@PathVariable int postId, @RequestHeader HttpHeaders headers) {
        try {
            User currentUser = authenticationHelper.tryGetUser(headers);
            postService.likePost(postId, currentUser);
        } catch (UnauthorizedOperationException uo) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, uo.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "Getting a single post",
            description = "This method is used for getting a certain post by Id.",
            parameters = {@Parameter(name = "postId", description = "this is the Id of the post you are trying to get.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PostView.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successful got a post."),
                    @ApiResponse(responseCode = "404",
                            description = "There is no such post.")}
    )
    public PostDto getPostById(@RequestHeader(required = false) HttpHeaders headers, @PathVariable int postId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            postService.tryViewingPost(postId, user.getId());
            Post post = postService.getById(postId);
            return postMapper.toDto(post);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (ResponseStatusException e){
            try {
                Post post = postService.getById(postId);
                return postMapper.toDto(post);
            }
            catch (EntityNotFoundException ee) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ee.getMessage());
            }
        }
    }

    @GetMapping()
    @Operation(
            summary = "Getting all posts",
            description = "This method is used for getting all non deleted posts.",
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PostView.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successful got all posts."),
                    @ApiResponse(responseCode = "404",
                            description = "There aren't any posts.")}
    )
    public List<PostDto> getAllPosts() {
        try {
            List<Post> posts = postService.getAll();
            return postMapper.toDtoList(posts);
        }
     catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
