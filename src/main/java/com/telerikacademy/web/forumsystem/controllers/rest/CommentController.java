package com.telerikacademy.web.forumsystem.controllers.rest;

import com.telerikacademy.web.forumsystem.exceptions.EntityNotFoundException;
import com.telerikacademy.web.forumsystem.exceptions.NotAllowedContentException;
import com.telerikacademy.web.forumsystem.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.forumsystem.helpers.AuthenticationHelper;
import com.telerikacademy.web.forumsystem.helpers.CommentMapper;
import com.telerikacademy.web.forumsystem.helpers.TextPurifier;
import com.telerikacademy.web.forumsystem.models.Comment;
import com.telerikacademy.web.forumsystem.models.CommentDto;
import com.telerikacademy.web.forumsystem.models.User;
import com.telerikacademy.web.forumsystem.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final AuthenticationHelper authenticationHelper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;
    private final TextPurifier textPurifier;

    @Autowired
    public CommentController(AuthenticationHelper authenticationHelper, CommentMapper commentMapper,
                             CommentService commentService, TextPurifier textPurifier) {
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
        this.textPurifier = textPurifier;
    }


    @GetMapping("/comment/{commentId}")
    @Operation(
            summary = "Get replies to a comment",
            description = "This method is used for getting all replies to a certain comment",
            parameters = {@Parameter(name = "commentId", description = "This is the Id of the comment you are trying get the replies of.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema = @Schema(implementation = CommentDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "Successful got some replies to a existing comment"),
                    @ApiResponse(responseCode = "404",
                            description = "There is no such comment or there aren't any replies to it.")}
    )
    public List<CommentDto> getRepliesToComment(@PathVariable int commentId) {
        try {
            return commentService.getByCommentId(commentId)
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());

        }
        catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/comment/{commentId}")
    @Operation(
            summary = "Replying on a comment",
            description = "This method is used for creating and adding a reply to a certain comment. Ensure the reply does not contain forbidden content.",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = CommentDto.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    ),
                    description = "Include content in the request body (min = 1, max = 500 characters)."
            ),
            parameters = {
                    @Parameter(name = "commentId", description = "The ID of the comment you are trying to reply to.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful reply creation and adding it to a comment", content = @Content(schema = @Schema(implementation = CommentDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                    @ApiResponse(responseCode = "404", description = "Comment not found - there is no such comment."),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the content in the reply contains forbidden content.")
            }
    )
    public CommentDto replyToComment(@RequestHeader HttpHeaders headers, @Valid @RequestBody CommentDto commentDto, @PathVariable int commentId) {
       try {
           User author = authenticationHelper.tryGetUser(headers);
           Comment comment = commentMapper.replyFromDto(commentDto, author, commentId);
           textPurifier.checkTextAndBan(comment.getContent(), author);
           commentService.create(comment,author);
           return commentMapper.toDto(comment);
       }
       catch (NotAllowedContentException e){
           throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
       }
       catch (UnauthorizedOperationException e) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
       }
       catch (EntityNotFoundException e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       }
    }
   @DeleteMapping("/comment/{commentId}")
   @Operation(
           summary = "Deleting a comment",
           description = "This method is used for deleting and removing a comment from a certain post. Ensure the user has the necessary permissions to delete the comment.",
           parameters = {
                   @Parameter(name = "commentId", description = "The ID of the comment you are trying to delete.", required = true, in = ParameterIn.PATH, schema = @Schema(type = "string"))
           },
           responses = {
                   @ApiResponse(responseCode = "200", description = "Successful deletion of the comment", content = @Content(schema = @Schema(implementation = CommentDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                   @ApiResponse(responseCode = "401", description = "Unauthorized access - wrong username or password."),
                   @ApiResponse(responseCode = "404", description = "Comment not found - there is no such comment."),
                   @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permission to delete this comment.")
           }
   )
    public void deleteComment(@RequestHeader HttpHeaders headers, @PathVariable int commentId) {
       try {
           User user = authenticationHelper.tryGetUser(headers);
           Comment comment = commentService.getById(commentId);
           commentService.delete(comment, user);
       }
       catch (UnauthorizedOperationException e) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
       }
       catch (EntityNotFoundException e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       }
    }
}