function AddVoteButtons() {
    var loggedInUser = AJS.$('input[title="loggedInUser"]').val();
    var issueID = AJS.$('input[name="id"]').val();
    //console.log(loggedInUser);
    var buttonsAdded = false;

    AJS.$('div[id|=comment][id!=comment-wiki-edit]').each(function () {
        var commentId = AJS.$(this).attr('id').split('-')[1];
        var commentUser = AJS.$(this).find('.action-details a').attr("rel");
        //console.log("commentUser - " + commentUser);
        AJS.$(this).find('.action-links').each(function () {
            if(AJS.$(this).find('.upvote').length == 0){
                //Add the buttons (only if the comment is from someone else)
                if (loggedInUser != commentUser) {
                    //console.log("Adding buttons");
                    buttonsAdded = true;
                    AJS.$(this).append(AJS.$('<a href="#" id="upvote" class="icon upvote" commentid=' + commentId + ' title="Up votes this comment">' +
                        '<img class="emoticon" src="' + AJS.contextPath() + '/images/icons/emoticons/thumbs_up.gif" height="16" width="16" align="absmiddle" alt="" border="0"></a>' +
                        '<a href="#" id="downvote" class="icon downvote" commentid=' + commentId + ' title="Down votes this comment">' +
                        '<img class="emoticon" src="' + AJS.contextPath() + '/images/icons/emoticons/thumbs_down.gif" height="16" width="16" align="absmiddle" alt="" border="0"></a>'));
                }
            } else {
                //console.log("buttons already added");
            }
        });
    });
    if(buttonsAdded) {
        AJS.$('.upvote').click(function (event) {
            event.preventDefault();
            AJS.$.ajax({
                url: AJS.contextPath() + "/rest/votecomments/latest/upvote?commentid=" + AJS.$(this).attr('commentid') + '&issueid=' + issueID,
                success: function () {
                    //console.log('Up voted');
                    ShowCurrentVotes();
                }
            });
        });

        AJS.$('.downvote').click(function (event) {
            event.preventDefault();
            AJS.$.ajax({
                url: AJS.contextPath() + "/rest/votecomments/latest/downvote?commentid=" + AJS.$(this).attr('commentid') + '&issueid=' + issueID,
                success: function () {
                    //console.log('Down voted');
                    ShowCurrentVotes();
                }
            });
        });
    }
}

function ShowCurrentVotes() {

    var issueID = AJS.$('input[name="id"]').val();
    var commentData = {};

    if (typeof issueID === "undefined") {

           // console.log("issueID  is undefined");

       } else {

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


}
function CheckPermissionsAndAdd(){
    AJS.$.getJSON(AJS.contextPath() + "/rest/api/latest/mypermissions?issueKey=" + AJS.Meta.get("issue-key"), function (data) {
        if(data.permissions.VIEW_VOTERS_AND_WATCHERS.havePermission){
            AddVoteButtons();
        }
    });

}

AJS.$('document').ready(function () {
    CheckPermissionsAndAdd();
    ShowCurrentVotes();
    JIRA.ViewIssueTabs.onTabReady(function(){
        CheckPermissionsAndAdd();
        ShowCurrentVotes();
    });
});