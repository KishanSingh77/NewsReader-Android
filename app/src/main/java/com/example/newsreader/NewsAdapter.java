package com.example.newsreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;
//import com.example.newsreader.News;


public class NewsAdapter extends ArrayAdapter<News> {


    public NewsAdapter(NewsActivity newsActivity, int news_item, List<News> newsList) {
        super(newsActivity , news_item , newsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        News news = getItem(position);
        ViewHolder viewHolder ;

        //inflate view
        if(convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_item , parent , false);
            viewHolder  = new ViewHolder();
            viewHolder.tv_title = convertView.findViewById(R.id.textView3_title);
            viewHolder. tv_author = convertView.findViewById(R.id.textView_author);
            viewHolder. tv_date = convertView.findViewById(R.id.textView2_date);
            viewHolder. iv_image = convertView.findViewById(R.id.imageView_newsImage);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder. tv_title.setText(news.title);
        viewHolder.tv_author.setText(news.author);
        viewHolder.tv_date.setText(news.publishedAt.split("T")[0]);
        Picasso.get().load(news.urlToImage).into(viewHolder.iv_image);


        return convertView ;
    }
    private static class ViewHolder{
        TextView tv_title;
        TextView tv_author;
        TextView tv_date ;
        ImageView iv_image ;
    }
}
