package tellh.com.gitclub.presentation.view.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import tellh.com.gitclub.R;
import tellh.com.gitclub.common.utils.DateUtils;
import tellh.com.gitclub.common.utils.LogUtils;
import tellh.com.gitclub.common.utils.StringUtils;
import tellh.com.gitclub.common.wrapper.ImageLoader;
import tellh.com.gitclub.common.wrapper.Note;
import tellh.com.gitclub.model.entity.Event;

import static tellh.com.gitclub.common.config.Constant.EventType;

public class NewsListAdapter extends BaseRecyclerAdapter<Event> {
    public NewsListAdapter(List<Event> items, Context context) {
        super(context, items);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_news;
    }

    @Override
    protected void bindData(RecyclerViewHolder holder, int position, final Event item) {
        holder.getView(R.id.item_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRepoActivity(item.getRepo().getName());
            }
        });
        ImageView imageView = holder.getImageView(R.id.iv_actor);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUserInfoActivity(item.getActor().getLogin());
            }
        });
        ImageLoader.load(item.getActor().getAvatar_url(), imageView);
        holder.setText(R.id.tv_actor, item.getActor().getLogin())
                .setText(R.id.tv_time_ago, DateUtils.timeAgo(item.getCreated_at()))
                .setText(R.id.tv_repo, item.getRepo().getName());
        handleEventType(holder, item);
    }

    private void handleEventType(RecyclerViewHolder holder, Event item) {
        //reset it
        holder.setText(R.id.tv_desc, "");
        try {
            Event.PayloadEntity payload = item.getPayload();
            switch (EventType.valueOf(item.getType())) {
                case WatchEvent:
                    holder.setText(R.id.tv_event, " starred");
                    break;
                case CreateEvent:
                    holder.setText(R.id.tv_event, "  created repo");
                    break;
                case CommitCommentEvent:
                    holder.setText(R.id.tv_event, " commented on")
                            .setText(R.id.tv_desc, payload.comment.body);
                    break;
                case ForkEvent:
                    holder.setText(R.id.tv_event, " forked")
                            .setText(R.id.tv_desc, StringUtils.append("to ", createReposSpan(payload.forkee.getFull_name())));
                    break;
                case GollumEvent:
                    holder.setText(R.id.tv_event, " created wiki page on");
                    break;
                case IssueCommentEvent:
                    holder.setText(R.id.tv_event, " commented")
                            .setText(R.id.tv_desc, StringUtils.append("on issue#",
                                    String.valueOf(payload.issue.number), ": ", payload.comment.body));
                    break;
                case IssuesEvent:
                    holder.setText(R.id.tv_event, StringUtils.append(payload.action, " issue"))
                            .setText(R.id.tv_desc, payload.issue.title);
                    break;
                case MemberEvent:
                    holder.setText(R.id.tv_event, "added collaborator to")
                            .setText(R.id.tv_desc, StringUtils.append("for ", payload.member.getLogin()));
                    break;
                case MembershipEvent:
                    holder.setText(R.id.tv_event, payload.action)
                            .setText(R.id.tv_desc, StringUtils.append("for ", payload.member.getLogin()));
                    break;
                case PublicEvent:
                    holder.setText(R.id.tv_event, " public");
                    break;
                case PullRequestEvent:
                    holder.setText(R.id.tv_event, StringUtils.append(payload.action, " pull request"))
                            .setText(R.id.tv_desc, StringUtils.append("title: ", payload.pull_request.title));
                    break;
                case PullRequestReviewCommentEvent:
                    holder.setText(R.id.tv_event, " commented on pull request")
                            .setText(R.id.tv_desc, payload.comment.body);
                    break;
                case PushEvent:
                    holder.setText(R.id.tv_event, " pushed to")
                            .setText(R.id.tv_desc, payload.ref);
                    break;
                case DeleteEvent:
                    holder.setText(R.id.tv_event, "  deleted repo");
                    break;
                case ReleaseEvent:
                    holder.setText(R.id.tv_event, "  released")
                            .setText(R.id.tv_desc, payload.release.body);
                    break;
                default:
                    holder.setText(R.id.tv_event, payload.action);
                    break;
            }
        } catch (IllegalArgumentException e) {
            LogUtils.e(e.getMessage());
        }
    }

//    private SpannableString createUserSpan(final String showText) {
//        SpannableString spanString = new SpannableString(showText);
//        spanString.setSpan(showText, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spanString.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                // TODO: 2016/9/9 start the user info activity
//                gotoUserInfoActivity(showText);
//            }
//        }, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return spanString;
//    }

    private SpannableString createReposSpan(final String showText) {
        SpannableString spanString = new SpannableString(showText);
        spanString.setSpan(showText, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // TODO: 2016/9/9 start the repo info activity
                gotoRepoActivity(showText);
            }
        }, 0, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    private void gotoRepoActivity(String repoFullName) {
        // TODO: 2016/9/9 start the repository info activity
        String[] pair = TextUtils.split(repoFullName, "/");
        if (pair.length != 2) {
            LogUtils.e("error in parse repo full name.");
            return;
        }
        Note.show("start the repository info activity");
    }

    protected void gotoUserInfoActivity(String user) {
        Note.show("start the user info activity");
    }
}
