package com.example.jazik.thebeach;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ArrayAdapter[][] mAdapter;
    ListView[][] lv;
    List<String[]>[] erik[];
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativeLayout1);
        int count = rl.getChildCount();
        lv = new ListView[count - 1][];
        mAdapter = new ArrayAdapter[count - 1][];
        erik = new List[count - 1][];
        for (int week = 0; week < count - 1; ++week) {
            LinearLayout ll = (LinearLayout) rl.getChildAt(week + 1);
            lv[week] = new ListView[ll.getChildCount()];
            mAdapter[week] = new ArrayAdapter[lv[week].length];
            erik[week] = new List[lv[week].length];

            for (int i = 0; i < lv[week].length; ++i) {
                lv[week][i] = (ListView) ll.getChildAt(i);
                erik[week][i] = new ArrayList<>();
//            mAdapter[i] = new Float(this, R.layout.timeslot, erik[i]);
                mAdapter[week][i] = new ArrayAdapter(this, R.layout.timeslot, android.R.id.text1, erik[week][i]) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        String[] text = (String[]) getItem(position);
                        text1.setText(text[0]);
                        text2.setText((text.length > 1) ? text[1] : "");
                        if (position == 0) {
//                        TextView day = (TextView) view.findViewById(R.id.day);
//                        day.setText(data[0]);
                            text1.setTextSize(14);
                            text1.setTextColor(0xffffff00);
                        } else {
                            text1.setTextSize(32);
                            text1.setTextColor(0xff00ff1f);
                        }
                        return view;
                    }
                };
                lv[week][i].setAdapter(mAdapter[week][i]);
//                lv[week][i].setClickable(true);
                lv[week][i].setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

//                    int day = i;
                        if (true) return;
                        Document doc = null;
                        try {
                            doc = Jsoup.connect("http://www.thebeach.se/boka/")
                                    .data("input_7.3", "Erik")
                                    .data("input_7.6", "Rosengren")
                                    .data("input_8", "0707704177")
                                    .data("input_9", "erikrosengren84@gmail.com")
                                    .data("input_3", "Ja, Guldkort")
                                    .data("input_18", "Ja, de som jag tänkt spela med har guldkort och byter vi ut nån spelare så säger jag till om den inte har guldkort")
                                    .data("input_5", "1")
                                    .data("input_1", "02/23/2017")
                                    .data("input_2[]", "21")
                                    .data("input_2[]", "00")
                                    .data("input_4[]", "")
                                    .data("input_4[]", "")
                                    .data("input_6", "Nej tack")
                                    .data("is_submit_68", "1")
                                    .data("gform_submit", "68")
                                    .data("gform_unique_id", "0")
                                    .data("state_68", "WyJbXSIsImU4ZmI3Yzc1NjdkZmNkMTBjNDJiYzBhODkwYmE3YWVhIl0=")
                                    .data("gform_target_page_number_68", "0")
                                    .data("gform_source_page_number_68", "1")
                                    .data("gform_field_values", "")
                                    .userAgent("Mozilla")
                                    .post();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(doc); // will print html source of homepage of facebook.
    /* write you handling code like...
    String st = "sdcard/";
    File f = new File(st+o.toString());
    // do whatever u want to do with 'f' File object
    */
                    }
                });
            }
        }
//        lv = (ListView) findViewById(R.id.listView);
//        lv.setAdapter(mAdapter);
        Thread bookFetchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Calendar calendar = Calendar.getInstance();
//                    Date today = calendar.getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String weekDay;
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                    Connection conn = Jsoup.connect("http://www.thebeach.se/spela_beachvolley/bokningslaget-just-nu/");
                    for (int week = 0; week < 2; ++week) {
                        for (int i = 0; i < lv[week].length; ++i) {
                            Date date = calendar.getTime();
                            String day = dateFormat.format(date);
                            float startSearchTime = 16.5f;
                            int weekdayIndex = calendar.get(Calendar.DAY_OF_WEEK);
                            if (weekdayIndex >= 2 && weekdayIndex <= 4)
                                startSearchTime = 16.0f; /* Monday - Wednesday */
                            if (weekdayIndex == 0 || weekdayIndex == 7)
                                startSearchTime = 10.5f; /* Saturday - Sunday */
                            Document doc = conn.data("date", day).post();
                            weekDay = dayFormat.format(calendar.getTime());
                            erik[week][i].add(new String[]{i == 0 && week == 0 ? "Idag" : weekDay});
                            Elements halfHourSlots = doc.getElementsByClass("sc_slot_box").not(".booked");
                            for (float time = startSearchTime; time < 22.5; time += 1.5) {
                                String slotEnd = "";
                                boolean found = true;
                                for (float halfHour = 0; halfHour <= 1; halfHour += 0.5) {
                                    found = false;
                                    float currentStartTime = time + halfHour;
                                    String halfHourSlot = "" + ((int) (currentStartTime));
                                    halfHourSlot += ":" + (currentStartTime % 1f == 0.5 ? "30" : "00");
                                    currentStartTime += 0.5;
                                    slotEnd = "" + ((int) (currentStartTime));
                                    slotEnd += ":" + (currentStartTime % 1f == 0.5 ? "30" : "00");
                                    halfHourSlot += " - " + slotEnd;
                                    for (Element e : halfHourSlots) {
                                        if (e.text().contains(halfHourSlot)) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found)
                                        break;
                                }
                                if (found) {
                                    erik[week][i].add(new String[]{"" + ((int) time), (time % 1f == 0.5 ? "30" : "")});
//                                erik[i].add(slotEnd);
                                }
                            }
                            MyRunnable obj = new MyRunnable(mAdapter[week][i]);
                            handler.post(obj);

                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bookFetchThread.start();
    }

    public class MyRunnable implements Runnable {
        private ArrayAdapter data;
        public MyRunnable(ArrayAdapter _data) {
            this.data = _data;
        }

        public void run() {
            data.notifyDataSetChanged();
        }
    }

}

