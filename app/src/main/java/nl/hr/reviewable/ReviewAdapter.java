package nl.hr.reviewable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by mick on 27/11/14.
 */
public class ReviewAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mReview;
    protected ViewHolder holder;

    public ReviewAdapter (Context context, List<ParseObject> review) {
        super(context, R.layout.review_listitem, review);
        mContext = context;
        mReview = review;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.review_listitem, null);
            holder = new ViewHolder();

            holder.usernameHomepage = (TextView) convertView
                    .findViewById(R.id.usernameHome);
            holder.titleHomepage = (TextView) convertView
                    .findViewById(R.id.titleHome);
//            holder.reviewHomepage = (TextView) convertView
//                    .findViewById(R.id.reviewHome);
            holder.tagsHomepage = (TextView) convertView
                    .findViewById(R.id.tagsHome);
            holder.imageHomepage = (ParseImageView) convertView
                    .findViewById(R.id.imageHome);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject reviewObject = mReview.get(position);

        if(reviewObject != null) {
            // Username
            String username = reviewObject.getString("user");
            holder.usernameHomepage.setText(username);

            // Title
            String title = reviewObject.getString("userTitle");
            holder.titleHomepage.setText(title);

//            // Review
//            String review = reviewObject.getString("userReview");
//            holder.reviewHomepage.setText(review);

            // Tags
            String tags = reviewObject.getString("userTags");
            holder.tagsHomepage.setText(tags);

            // Image
            ParseFile image = reviewObject.getParseFile("userImageFile");
            holder.imageHomepage.setParseFile(image);

            holder.imageHomepage.loadInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                }
            });
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView usernameHomepage;
        TextView titleHomepage;
        //TextView reviewHomepage;
        TextView tagsHomepage;
        ParseImageView imageHomepage;
    }

}
