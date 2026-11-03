package com.legion.controlcenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.media.AudioManager;
import android.hardware.camera2.CameraManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

public class MainActivity extends Activity {
    private static final int PICK_CHARACTER = 42;
    private SharedPreferences prefs;
    private ImageView character;
    private FrameLayout root;
    private float charScale = 1f;
    private int charX = 0, charY = 0;
    private boolean flashlight = false;
    private static final int CAMERA_PERMISSION = 71;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("legion", MODE_PRIVATE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(5894 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        buildUi();
    }

    private GradientDrawable shape(int fill, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{fill, Color.rgb(24, 5, 12), Color.rgb(8, 3, 7)});
        d.setCornerRadius(radius); d.setStroke(2, stroke); return d;
    }
    private TextView text(String s, int size, boolean bold) {
        TextView v = new TextView(this); v.setText(s); v.setTextColor(Color.WHITE); v.setTextSize(size);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setGravity(Gravity.CENTER); return v;
    }
    private TextView tile(String label, String sub, Runnable action) {
        TextView v = text(label + (sub.isEmpty()?"":"\n"+sub), 17, true);
        v.setBackground(shape(0xDD160811, 0xFFFF3155, 24)); v.setPadding(10, 8, 10, 8);
        v.setOnClickListener(w -> action.run()); return v;
    }
    private void buildUi() {
        root = new FrameLayout(this);
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF18040B,0xFF050205,0xFF260610}));
        LinearLayout main = new LinearLayout(this); main.setOrientation(LinearLayout.HORIZONTAL); main.setPadding(16,12,16,12);
        root.addView(main, new FrameLayout.LayoutParams(-1,-1));

        FrameLayout left = new FrameLayout(this);
        left.setBackground(shape(0x551B0710,0x66FF3155,28));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,-1,0.39f); lp.setMargins(0,0,12,0); main.addView(left,lp);
        TextView logo = text("◆  LEGION  ◆",27,true); logo.setTextColor(0xFFFF637B);
        FrameLayout.LayoutParams glp = new FrameLayout.LayoutParams(-1,60,Gravity.TOP); left.addView(logo,glp);
        character = new ImageView(this); character.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1,-1); cp.setMargins(8,58,8,8); left.addView(character,cp);
        String saved = prefs.getString("character", null);
        if (saved != null) { try { character.setImageURI(Uri.parse(saved)); } catch(Exception ignored){} }
        character.setOnTouchListener(new View.OnTouchListener() {
            float sx, sy; int bx, by;
            public boolean onTouch(View v, android.view.MotionEvent e) {
                if(e.getAction()==0){sx=e.getRawX();sy=e.getRawY();bx=charX;by=charY;return true;}
                if(e.getAction()==2){charX=bx+(int)(e.getRawX()-sx);charY=by+(int)(e.getRawY()-sy);v.setTranslationX(charX);v.setTranslationY(charY);return true;} return true;
            }
        });
        TextView change = tile("＋  CHANGE CHARACTER", "PNG / WebP transparent", () -> pickCharacter());
        FrameLayout.LayoutParams chp = new FrameLayout.LayoutParams(-1,54,Gravity.BOTTOM); chp.setMargins(12,0,12,12); left.addView(change,chp);

        LinearLayout right = new LinearLayout(this); right.setOrientation(LinearLayout.VERTICAL); main.addView(right,new LinearLayout.LayoutParams(0,-1,0.61f));
        LinearLayout top = new LinearLayout(this); top.setOrientation(LinearLayout.HORIZONTAL); right.addView(top,new LinearLayout.LayoutParams(-1,0,1.0f));
        LinearLayout col1 = new LinearLayout(this); col1.setOrientation(LinearLayout.VERTICAL); top.addView(col1,new LinearLayout.LayoutParams(0,-1,1));
        LinearLayout col2 = new LinearLayout(this); col2.setOrientation(LinearLayout.VERTICAL); LinearLayout.LayoutParams c2p=new LinearLayout.LayoutParams(0,-1,1); c2p.setMargins(10,0,0,0); top.addView(col2,c2p);
        addPair(col1,"◉  Wi-Fi","Open settings",()->openPanel(Settings.Panel.ACTION_WIFI));
        addPair(col1,"ᛒ  Bluetooth","Bluetooth settings",()->openSettings(Settings.ACTION_BLUETOOTH_SETTINGS));
        addPair(col2,"⇅  Mobile data","Internet settings",()->openPanel(Settings.Panel.ACTION_INTERNET_CONNECTIVITY));
        addPair(col2,"◉  Hotspot","Hotspot settings",()->openSettings("android.settings.TETHER_SETTINGS"));

        LinearLayout middle = new LinearLayout(this); middle.setOrientation(LinearLayout.HORIZONTAL); LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,0,1.05f); mp.setMargins(0,10,0,10); right.addView(middle,mp);
        LinearLayout sliders = new LinearLayout(this); sliders.setOrientation(LinearLayout.HORIZONTAL); middle.addView(sliders,new LinearLayout.LayoutParams(0,-1,0.42f));
        addSlider(sliders,"☀ Brightness",true); addSlider(sliders,"◖ Volume",false);
        TextView media = tile("♫\nNOT PLAYING\n\n|◀       ▶       ▶|", "Media controls", () -> openSettings("android.settings.SETTINGS"));
        LinearLayout.LayoutParams medp=new LinearLayout.LayoutParams(0,-1,0.58f); medp.setMargins(10,0,0,0); middle.addView(media,medp);

        LinearLayout bottom = new LinearLayout(this); bottom.setOrientation(LinearLayout.VERTICAL); bottom.setPadding(8,8,8,8); bottom.setBackground(shape(0xCC10050A,0xFFFF3155,24)); right.addView(bottom,new LinearLayout.LayoutParams(-1,0,1.5f));
        String[][] rows={{"✈\nAirplane","⟳\nRotate","▤\nScreenshot","☼\nFlashlight"},{"◐\nDark mode","⌖\nLocation","▣\nScreen record","☾\nDND"}};
        for(int r=0;r<rows.length;r++){ LinearLayout row=new LinearLayout(this); bottom.addView(row,new LinearLayout.LayoutParams(-1,0,1)); for(int c=0;c<4;c++){ final String label=rows[r][c]; TextView t=tile(label,"",()->handleQuick(label)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1); p.setMargins(4,4,4,4); row.addView(t,p); }}
        TextView close=text("⌄   CLOSE",14,true); close.setTextColor(0xFFFF8193); close.setPadding(0,6,0,0); right.addView(close,new LinearLayout.LayoutParams(-1,30)); close.setOnClickListener(v->finish());
        setContentView(root);
    }
    private void addPair(LinearLayout parent,String title,String sub,Runnable action){ TextView t=tile(title,sub,action); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,0,1); p.setMargins(0,4,0,4); parent.addView(t,p); }
    private void addSlider(LinearLayout parent,String label,boolean bright){ LinearLayout box=new LinearLayout(this); box.setGravity(Gravity.CENTER); box.setOrientation(LinearLayout.VERTICAL); box.setBackground(shape(0xCC180811,0xFFFF526D,22)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1); p.setMargins(3,0,3,0); parent.addView(box,p); TextView title=text(label,12,true); box.addView(title); SeekBar bar=new SeekBar(this); bar.setMax(100); bar.setProgress(bright?70:45); box.addView(bar,new LinearLayout.LayoutParams(-1,90)); if(!bright){bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int n,boolean u){if(u){AudioManager a=(AudioManager)getSystemService(AUDIO_SERVICE);a.setStreamVolume(AudioManager.STREAM_MUSIC,(int)(a.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*n/100f),0);}} public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}});} else {bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int n,boolean u){if(u && Settings.System.canWrite(MainActivity.this)){Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,(int)(n*2.55));}} public void onStartTrackingTouch(SeekBar s){} public void onStopTrackingTouch(SeekBar s){}}); title.setOnClickListener(v->{if(!Settings.System.canWrite(this))startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,Uri.parse("package:"+getPackageName())));});}}
    private void addRow(LinearLayout parent,String[] labels){ }
    private void handleQuick(String label){ if(label.contains("Flashlight")){toggleFlash();} else if(label.contains("Screenshot")){Toast.makeText(this,"Use the tablet's screenshot shortcut",Toast.LENGTH_SHORT).show();} else if(label.contains("Airplane")){openPanel(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);} else if(label.contains("Location")){openSettings(Settings.ACTION_LOCATION_SOURCE_SETTINGS);} else if(label.contains("Rotate")){openSettings(Settings.ACTION_DISPLAY_SETTINGS);} else if(label.contains("Dark")){openSettings("android.settings.DISPLAY_SETTINGS");} else if(label.contains("DND")){openSettings("android.settings.ZEN_MODE_SETTINGS");} else if(label.contains("record")){Toast.makeText(this,"Use Android screen recorder from system Quick Settings",Toast.LENGTH_LONG).show();} }
    private void toggleFlash(){if(android.os.Build.VERSION.SDK_INT>=23 && checkSelfPermission(android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{android.Manifest.permission.CAMERA},CAMERA_PERMISSION);return;} try{CameraManager cm=(CameraManager)getSystemService(Context.CAMERA_SERVICE);String id=cm.getCameraIdList()[0];flashlight=!flashlight;cm.setTorchMode(id,flashlight);}catch(Exception e){Toast.makeText(this,"Flashlight unavailable",Toast.LENGTH_SHORT).show();}}
    private void openPanel(String action){try{startActivity(new Intent(action));}catch(Exception e){openSettings(Settings.ACTION_SETTINGS);}}
    private void openSettings(String action){try{startActivity(new Intent(action));}catch(Exception e){startActivity(new Intent(Settings.ACTION_SETTINGS));}}
    private void pickCharacter(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("image/*");startActivityForResult(i,PICK_CHARACTER);}
    @Override protected void onActivityResult(int r,int c,Intent data){super.onActivityResult(r,c,data);if(r==PICK_CHARACTER&&c==RESULT_OK&&data!=null&&data.getData()!=null){Uri u=data.getData();try{getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){} prefs.edit().putString("character",u.toString()).apply();character.setImageURI(u);}}
}
