package nl.hr.reviewable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by mick on 27/11/14.
 */
public class ReviewAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mReview;
    protected ImageView imageHome;

    public ReviewAdapter (Context context, List<ParseObject> review) {
        super(context, R.layout.review_listitem, review);
        mContext = context;
        mReview = review;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.review_listitem, null);
            holder = new ViewHolder();
            holder.usernameHomepage = (TextView) convertView
                    .findViewById(R.id.usernameHome);
            holder.titleHomepage = (TextView) convertView
                    .findViewById(R.id.titleHome);
            holder.reviewHomepage = (TextView) convertView
                    .findViewById(R.id.reviewHome);
            holder.tagsHomepage = (TextView) convertView
                    .findViewById(R.id.tagsHome);
            //holder.imageHomepage = (ImageView) convertView
                    //.findViewById(R.id.imageHome);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject reviewObject = mReview.get(position);

        // Username
        String username = reviewObject.getString("user");
        holder.usernameHomepage.setText(username);

        // Title
        String title = reviewObject.getString("userTitle");
        holder.titleHomepage.setText(title);

        // Review
        String review = reviewObject.getString("userReview");
        holder.reviewHomepage.setText(review);

        // Tags
        String tags = reviewObject.getString("userTags");
        holder.tagsHomepage.setText(tags);

        // Image
        ParseFile image = (ParseFile)reviewObject.getParseFile("userImageFile");
        image.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if(e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageHome.findViewById(R.id.imageHome);
                    imageHome.setImageBitmap(bmp);
                    //holder.imageHomepage.setParseFile(bmp);
                }
                else {
                    //wrong
                }
            }
        });



        return convertView;
    }

    public static class ViewHolder {
        TextView usernameHomepage;
        TextView titleHomepage;
        TextView reviewHomepage;
        TextView tagsHomepage;
        //ImageView imageHomepage;
    }

}
