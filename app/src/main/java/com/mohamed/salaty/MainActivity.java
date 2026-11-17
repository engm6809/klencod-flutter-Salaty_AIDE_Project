package com.mohamed.salaty;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends Activity {
    private final int navy = Color.rgb(12, 31, 66);
    private final int gold = Color.rgb(220, 178, 91);
    private LinearLayout root, content;
    private TextView nextPrayer;
    private String city = "القاهرة";
    private String[] prayerNames = {"الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء"};
    private String[] prayerTimes = {"05:00", "06:25", "12:00", "15:15", "17:35", "18:55"};

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        showHome();
        new PrayerTask().execute("Cairo", "Egypt");
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius);
        return d;
    }

    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        return l;
    }

    private void init(String title) {
        root = column();
        root.setBackgroundColor(Color.rgb(245,247,251));
        TextView header = text("☾  صلاتي", 25, Color.WHITE, true);
        header.setBackgroundColor(navy);
        header.setPadding(12,22,12,22);
        root.addView(header, new LinearLayout.LayoutParams(-1, -2));
        TextView sub = text(title, 18, navy, true);
        sub.setPadding(12, 16, 12, 10);
        root.addView(sub);
        content = column();
        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout nav = new LinearLayout(this);
        nav.setBackgroundColor(navy);
        String[] labels = {"الرئيسية", "القرآن", "الأذكار", "الإعدادات"};
        for (int i=0;i<labels.length;i++) {
            final int n=i;
            TextView item=text(labels[i],13,Color.WHITE,true);
            item.setPadding(2,15,2,15);
            nav.addView(item,new LinearLayout.LayoutParams(0,-2,1));
            item.setOnClickListener(v -> {
                if(n==0) showHome(); else if(n==1) showQuran();
                else if(n==2) showAzkar(); else showSettings();
            });
        }
        root.addView(nav);
        setContentView(root);
    }

    private TextView cardTitle(String s) {
        TextView t=text(s,19,navy,true);
        t.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        t.setPadding(18,14,18,14);
        t.setBackground(bg(Color.WHITE,22));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(14,8,14,8);
        content.addView(t,p);
        return t;
    }

    private void showHome() {
        init("رفيقك في الصلاة");
        TextView cityView=text("📍 "+city+"  |  تغيير المدينة من الإعدادات",15,Color.DKGRAY,false);
        content.addView(cityView);
        TextView hero=text("﴿ إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَوْقُوتًا ﴾",19,Color.WHITE,true);
        hero.setBackground(bg(navy,24));
        hero.setPadding(18,28,18,28);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2); hp.setMargins(14,10,14,12);
        content.addView(hero,hp);
        cardTitle("مواقيت الصلاة");
        for(int i=0;i<prayerNames.length;i++){
            TextView row=text(prayerNames[i]+"                         "+prayerTimes[i],17,navy,true);
            row.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            row.setBackground(bg(Color.WHITE,14));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
            p.setMargins(14,3,14,3);
            content.addView(row,p);
        }
        TextView note=text("يتم جلب المواقيت عبر الإنترنت عند توفر الاتصال. راجع المدينة وطريقة الحساب قبل الاعتماد عليها.",12,Color.GRAY,false);
        content.addView(note);
        cardTitle("حديث اليوم");
        TextView hadith=text("«أحب الأعمال إلى الله الصلاة على وقتها»",17,navy,false);
        content.addView(hadith);
        TextView credit=text("من تصميم وتطوير\nمهندس محمد مصطفى",13,Color.GRAY,false);
        content.addView(credit);
    }

    private void showQuran() {
        init("القرآن الكريم");
        String[] surahs={"الفاتحة","البقرة","آل عمران","النساء","المائدة","الأنعام","الأعراف","الأنفال","التوبة","يونس","هود","يوسف","الرعد","إبراهيم","الحجر","النحل","الإسراء","الكهف","مريم","طه"};
        cardTitle("اختر سورة");
        for(String s:surahs){
            TextView v=text("سورة "+s+"  ◀",18,navy,true);
            v.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
            v.setBackground(bg(Color.WHITE,16));
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(14,4,14,4);
            content.addView(v,p);
            v.setOnClickListener(x -> showSurah(s));
        }
        TextView disclaimer=text("هذه نسخة أولية: يلزم إضافة نص المصحف الموثوق كاملًا قبل النشر.",12,Color.GRAY,false);
        content.addView(disclaimer);
    }

    private void showSurah(String name) {
        init("سورة "+name);
        TextView verse=text("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",25,navy,true);
        content.addView(verse);
        TextView msg=text("لم يتم تضمين نص المصحف في هذه النسخة لتجنب عرض نص قرآني ناقص أو غير موثّق. يمكن ربط مصدر قرآن موثوق في الخطوة التالية.",17,Color.DKGRAY,false);
        content.addView(msg);
        Button back=new Button(this); back.setText("العودة للسور");
        content.addView(back); back.setOnClickListener(v -> showQuran());
    }

    private void showAzkar() {
        init("الأذكار");
        String[][] data={
            {"أذكار الصباح","أصبحنا وأصبح الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له."},
            {"أذكار المساء","أمسينا وأمسى الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له."},
            {"التسبيح","سبحان الله وبحمده، سبحان الله العظيم."},
            {"الاستغفار","أستغفر الله العظيم وأتوب إليه."},
            {"الصلاة على النبي","اللهم صل وسلم وبارك على نبينا محمد."}
        };
        for(String[] item:data){
            cardTitle(item[0]);
            TextView t=text(item[1],19,navy,false);
            content.addView(t);
        }
    }

    private void showSettings() {
        init("الإعدادات");
        cardTitle("المدينة");
        EditText cityInput=new EditText(this);
        cityInput.setHint("اكتب اسم المدينة");
        cityInput.setText(city);
        content.addView(cityInput);
        Button save=new Button(this); save.setText("حفظ المدينة وجلب المواقيت");
        content.addView(save);
        save.setOnClickListener(v -> {
            city=cityInput.getText().toString().trim();
            if(city.length()==0) city="القاهرة";
            new PrayerTask().execute(city,"Egypt");
            showHome();
        });
        cardTitle("عن التطبيق");
        content.addView(text("صلاتي\nمن تصميم وتطوير مهندس محمد مصطفى\nنسخة تجريبية أولية",16,Color.DKGRAY,false));
        cardTitle("ملاحظات");
        content.addView(text("تأكد من اختيار المدينة الصحيحة ومراجعة طريقة الحساب والتوقيت الصيفي. التنبيهات الصوتية والقرآن الكامل والتفسير والنسخ الاحتياطي لم تُفعّل بعد في هذه النسخة.",14,Color.GRAY,false));
    }

    private class PrayerTask extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... a) {
            HttpURLConnection c=null;
            try {
                String u="https://api.aladhan.com/v1/timingsByCity?city="+java.net.URLEncoder.encode(a[0],"UTF-8")+"&country="+java.net.URLEncoder.encode(a[1],"UTF-8")+"&method=5";
                c=(HttpURLConnection)new URL(u).openConnection();
                c.setConnectTimeout(8000); c.setReadTimeout(8000);
                Scanner sc=new Scanner(c.getInputStream()).useDelimiter("\\A");
                return sc.hasNext()?sc.next():"";
            } catch(Exception e){ return ""; }
            finally { if(c!=null)c.disconnect(); }
        }
        protected void onPostExecute(String result) {
            try {
                JSONObject root=new JSONObject(result).getJSONObject("data").getJSONObject("timings");
                String[] keys={"Fajr","Sunrise","Dhuhr","Asr","Maghrib","Isha"};
                for(int i=0;i<keys.length;i++) prayerTimes[i]=root.getString(keys[i]).substring(0,5);
                if(content!=null) showHome();
            } catch(Exception ignored) {}
        }
    }
}
