function AddVoteButtons() {
    var loggedInUser = AJS.$('input[title="loggedInUser"]').val();
    var issueID = AJS.$('input[name="id"]').val();

    AJS.$('div[id|=comment][id!=comment-wiki-edit]').each(function () {
        var commentId = AJS.$(this).attr('id').split('-')[1];
        var commentUser = AJS.$(this).find('.action-details a').attr("rel");
        console.log("commentUser - " + commentUser);
        AJS.$(this).find('.action-links').each(function () {
            //Add the buttons (only if the comment is from someone else)

            if (loggedInUser != commentUser) {
                AJS.$(this).append(AJS.$('<a href="#" class="icon upvote" commentid=' + commentId + ' title="Up votes this comment">' +
                    '<img class="emoticon" src="' + AJS.contextPath() + '/images/icons/emoticons/thumbs_up.gif" height="19" width="19" align="absmiddle" alt="" border="0"></a>' +
                    '<a href="#" class="icon downvote" commentid=' + commentId + ' title="Down votes this comment">' +
                    '<img class="emoticon" src="' + AJS.contextPath() + '/images/icons/emoticons/thumbs_down.gif" height="19" width="19" align="absmiddle" alt="" border="0"></a>'));
            }
        });
    });

    AJS.$('.upvote').click(function (event) {
        event.preventDefault();
        AJS.$.ajax({
            url: AJS.contextPath() + "/rest/votecomments/latest/upvote?commentid=" + AJS.$(this).attr('commentid') + '&issueid=' + issueID,
            success: function () {
                console.log('Up voted');
                ShowCurrentVotes();
            }
        });
    });

    AJS.$('.downvote').click(function (event) {
        event.preventDefault();
        AJS.$.ajax({
            url: AJS.contextPath() + "/rest/votecomments/latest/downvote?commentid=" + AJS.$(this).attr('commentid') + '&issueid=' + issueID,
            success: function () {
                console.log('Down voted');
                ShowCurrentVotes();
            }
        });
    });
}

function ShowCurrentVotes() {
    var issueID = AJS.$('input[name="id"]').val();
    var commentData = {};

    AJS.$.getJSON(AJS.contextPath() + "/rest/votecomments/latest/commentsvotes?issueid=" + issueID, function (data) {
            AJS.$.each(data, function () {
                commentData['comment-' + this.commentid] = this;
            });
            AJS.$('.currentvotes').remove();

            AJS.$('div[id|=comment][id!=comment-wiki-edit]').each(function () {
                var commentId = AJS.$(this).attr('id').split('-')[1];
                AJS.$(this).find('.action-links').each(function () {
                    //Add the current votes
                    var cmData = commentData["comment-" + commentId];

                    if (cmData && cmData.downvotes) {
                        AJS.$(this).before(
                            AJS.$('<div class="currentvotes dislikes">' + cmData.downvotes + ' dislike(s)</div>')
                        );
                    }
                    if (cmData && cmData.upvotes) {
                        AJS.$(this).before(
                            AJS.$('<div class="currentvotes likes">' + cmData.upvotes + ' like(s)</div>')
                        );
                    }
                });
            });
        }
    );
}

AJS.$('document').ready(function () {
    AddVoteButtons();
    ShowCurrentVotes();
});