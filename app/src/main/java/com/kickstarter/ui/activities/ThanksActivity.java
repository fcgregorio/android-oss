package com.kickstarter.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kickstarter.R;
import com.kickstarter.libs.BaseActivity;
import com.kickstarter.libs.RequiresPresenter;
import com.kickstarter.libs.vendor.TweetComposer;
import com.kickstarter.models.Project;
import com.kickstarter.presenters.ThanksPresenter;
import com.kickstarter.ui.adapters.ProjectCardMiniAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresPresenter(ThanksPresenter.class)
public class ThanksActivity extends BaseActivity<ThanksPresenter> {
  @Bind(R.id.backed_project) TextView backedProjectTextView;
  @Bind(R.id.recommended_projects_recycler_view) RecyclerView recommendedProjectsRecyclerView;
  @Bind(R.id.woohoo_background) ImageView woohooBackgroundImageView;

  ProjectCardMiniAdapter projectCardMiniAdapter;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.thanks_layout);
    ButterKnife.bind(this);

    final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    recommendedProjectsRecyclerView.setLayoutManager(layoutManager);

    displayWoohooBackground();

    presenter.takeProject(getIntent().getExtras().getParcelable(getString(R.string.intent_project)));
  }

  public void show(final Project project) {
    backedProjectTextView.setText(Html.fromHtml(getString(R.string.You_just_backed, project.name())));
  }

  public void showRecommendedProjects(final List<Project> projects) {
    projectCardMiniAdapter = new ProjectCardMiniAdapter(this, projects);
    recommendedProjectsRecyclerView.setAdapter(projectCardMiniAdapter);
  }

  public void onDoneClick(final View view) {
    presenter.takeDoneClick();
  }

  public void onShareClick(final View view) {
    presenter.takeShareClick();
  }

  @OnClick(R.id.twitter_button)
  public void onTwitterButtonClick(final View view) {
    presenter.takeTwitterClick();
  }

  public void startShareIntent(final Project project) {
    final Intent intent = new Intent(android.content.Intent.ACTION_SEND)
      .setType("text/plain")
      .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
      .putExtra(Intent.EXTRA_TEXT, shareString(project));

    startActivity(Intent.createChooser(intent, getString(R.string.Share_this_project)));
  }

  public void startTwitterShareIntent(final Project project) {
    new TweetComposer.Builder(this).text(shareString(project)).show();
  }

  public void startDiscoveryActivity() {
    final Intent intent = new Intent(this, DiscoveryActivity.class)
      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  private String shareString(final Project project) {
    return getString(R.string.I_just_backed_project_on_Kickstarter, project.name(), project.secureWebProjectUrl());
  }

  private void displayWoohooBackground() {
    new Handler().postDelayed(() -> {
        woohooBackgroundImageView.animate().setDuration(Long.parseLong(getString(R.string.woohoo_duration))).alpha(1);
        final Drawable drawable = woohooBackgroundImageView.getDrawable();
        if (drawable instanceof Animatable) {
          ((Animatable) drawable).start();
        }
      }, 500);
  }
}
