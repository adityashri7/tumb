package android.tumb.com.tumb.Main.Feed;

/**
 * Created by trust on 8/27/2016.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.tumb.com.tumb.Data.PostSerializable;
import android.tumb.com.tumb.Data.PostWrapper;
import android.tumb.com.tumb.Misc.PostWrapperToSerializable;
import android.tumb.com.tumb.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final List<PostWrapper> mValues;
    private Context context;


    public FeedAdapter(List<PostWrapper> items, Context context) {
        mValues = items;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PostWrapper wrapper = mValues.get(position);


        holder.blogName.setText(wrapper.getBlog());
        holder.tags.setText(wrapper.getTags());
        holder.notesAmount.setText(wrapper.getNotes());
        holder.postSource.setText(wrapper.getSource());
        holder.photoCaption.setText(wrapper.getCaption());
        holder.blogAvatar.setVisibility(View.VISIBLE);
        Glide.with(holder.blogAvatar.getContext())
                .load(wrapper.getBlogAvatar())
                .into(holder.feedImage);


        write(wrapper, position);

        if (mValues.get(position).getType().equals("photo")){
            String photoUri = wrapper.getPhotoUrl();
            holder.feedImage.setVisibility(View.VISIBLE);
            Glide.with(holder.feedImage.getContext())
                    .load(photoUri)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.feedImage);
        }
        else {
            holder.feedImage.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        }
        else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView blogName;
        public final TextView postSource;
        public final TextView tags;
        public final TextView notesAmount;
        public final ImageView feedImage;
        public final ImageView blogAvatar;
        public final TextView photoCaption;

        public ViewHolder(View view) {
            super(view);
            blogName = (TextView) view.findViewById(R.id.blog_name);
            postSource = (TextView) view.findViewById(R.id.post_source);
            tags = (TextView) view.findViewById(R.id.tags);
            notesAmount = (TextView) view.findViewById(R.id.notes_amount);
            blogAvatar = (ImageView) view.findViewById(R.id.blog_avatar);
            feedImage = (ImageView) view.findViewById(R.id.feed_image);
            photoCaption = (TextView) view.findViewById(R.id.photo_caption);
        }

    }

    public void write(PostWrapper wrapper, int pos){
        PostSerializable serializable = (new PostWrapperToSerializable(wrapper)).getSerializable();
        String filename = "postFile_" + pos + ".srl";
        ObjectOutput out = null;
        File root = context.getFilesDir();
        String dirPath = context.getFilesDir().getAbsolutePath() + File.separator + "cache";
        File projDir = new File(dirPath);
        if (!projDir.exists()){
            projDir.mkdir();
        }

        try {
            out = new ObjectOutputStream(new FileOutputStream(projDir+File.separator + filename));
            out.writeObject(serializable);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
