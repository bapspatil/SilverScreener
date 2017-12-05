package bapspatil.silverscreener.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import bapspatil.silverscreener.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutMeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        (findViewById(R.id.play_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=7368032842071222295");
                Intent intentToPlayStore = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentToPlayStore);
            }
        });
        (findViewById(R.id.github_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/bapspatil");
                Intent intentToGitHub = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentToGitHub);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
