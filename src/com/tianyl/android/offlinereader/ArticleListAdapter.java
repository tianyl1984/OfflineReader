package com.tianyl.android.offlinereader;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianyl.android.offlinereader.common.StringUtil;
import com.tianyl.android.offlinereader.model.Article;

public class ArticleListAdapter extends BaseAdapter {

	private List<Article> articles;

	private Context ctx;

	public ArticleListAdapter(List<Article> articles, Context ctx) {
		this.articles = articles;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return articles.size();
	}

	@Override
	public Article getItem(int position) {
		return articles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater li = LayoutInflater.from(ctx);
			convertView = li.inflate(R.layout.articleitem, null);
		}
		Article article = getItem(position);
		((TextView) convertView.findViewById(R.id.titleTextView)).setText(StringUtil.htmlDecode(article.getTitle()));
		if (!article.isFinish()) {
			((TextView) convertView.findViewById(R.id.titleTextView)).setTextColor(ctx.getResources().getColor(android.R.color.darker_gray));
		} else {
			((TextView) convertView.findViewById(R.id.titleTextView)).setTextColor(ctx.getResources().getColor(android.R.color.black));
		}
		return convertView;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
		this.notifyDataSetChanged();
	}

}
