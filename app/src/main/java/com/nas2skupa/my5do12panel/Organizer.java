package com.nas2skupa.my5do12panel;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Organizer extends BaseActivity implements OnClickListener {
    private static final String tag = "SimpleCalendarViewActivity";

    private ImageView calendarToJournalButton;
    private Button selectedDayMonthYearButton;
    private TextView ordersDetails;
    private RelativeLayout ordersLayout;
    private LinearLayout orderConfirmation;
    private ImageButton orderConfirm;
    private ImageButton orderCancel;
    private ImageButton orderSuggest;
    private Button currentMonth;
    private Button currentDay;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private GridCellAdapter adapter;
    private Calendar _calendar;
    private int month, year;
    private final DateFormat dateFormatter = new DateFormat();
    private static final String dateTemplate = "MMMM yyyy";

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.nas2skupa.my5do12panel.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "example.com";
    // The account name
    public static final String ACCOUNT = "default_account";
    // Instance fields
    Account mAccount;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.organizer);
        SyncUtils.CreateSyncAccount(this);

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
        Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: " + year);

//        selectedDayMonthYearButton = (Button) this.findViewById(R.id.selectedDayMonthYear);
//        selectedDayMonthYearButton.setText("Selected: ");

        prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (Button) this.findViewById(R.id.currentMonth);
        currentDay = (Button) this.findViewById(R.id.currentDay);

        nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) this.findViewById(R.id.calendar);

        // Initialised
        adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);

        ordersDetails = (TextView) this.findViewById(R.id.eventsDetails);
        ordersDetails.setMovementMethod(new ScrollingMovementMethod());

        orderConfirmation = (LinearLayout) this.findViewById(R.id.orderConfirmation);
//        Drawable d = getResources().getDrawable(R.drawable.nar_potvrdi);
//        d.setColorFilter(R.color.calendar_text_light, PorterDuff.Mode.DST_ATOP);

        orderConfirm = (ImageButton) this.findViewById(R.id.orderConfirm);
        orderConfirm.setOnClickListener(orderConfirmationListener);
        orderCancel = (ImageButton) this.findViewById(R.id.orderCancel);
        orderCancel.setOnClickListener(orderConfirmationListener);
        orderSuggest = (ImageButton) this.findViewById(R.id.orderSuggest);

        ordersLayout = (RelativeLayout) this.findViewById(R.id.eventsLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        while (GcmIntentService.class != null && !GcmIntentService.pendingNotifications.isEmpty()) {
            Intent notification = GcmIntentService.pendingNotifications.pop();
            showNotificationDialog(notification.getExtras());
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notification.getIntExtra("notificationId", -1));
        }
    }

    OnClickListener orderConfirmationListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Order order = (Order) orderConfirmation.getTag();
            if (view == orderConfirm)
                showConfirmationDialog(order);
            else if (view == orderCancel) {
                showCancelDialog(order);
            }
        }
    };

    public void showConfirmationDialog(Order order) {
        final String orderId = order.id;
        final EditText note = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Potvrda termina")
                .setMessage("Unesite poruku za korisnika:")
                .setView(note)
                .setPositiveButton("Potvrdi termin", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendConfirmation(orderId, "1", note.getText().toString());
                        orderConfirmation.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("Odustani", null)
                .show();
    }

    public void showCancelDialog(Order order) {
        final String orderId = order.id;
        final EditText note = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Otkazivanje termina")
                .setMessage("Unesite poruku za korisnika:")
                .setView(note)
                .setPositiveButton("Otkaži termin", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendConfirmation(orderId, "2", note.getText().toString());
                        orderConfirmation.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("Odustani", null)
                .show();
    }

    public void showNotificationDialog(Bundle data) {
        SimpleDateFormat inputDateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        SimpleDateFormat outputDateFormatter = new SimpleDateFormat("dd.MM.yyyy.");

        final String orderId = data.getString("orderId");
        String provider = data.getString("provider");
        boolean confirmed = data.getString("confirmed").equals("1");
        String date = null;
        try {
            date = outputDateFormatter.format(inputDateFormatter.parse(data.getString("date")));
        } catch (ParseException e) {
        }
        String startTime = data.getString("startTime");
        String endTime = data.getString("endTime");
        String message = String.format("Vaš termin %s u %s sati je %s", date, startTime.substring(0, 5), confirmed ? "potvrđen." : "otkazan.");
        new AlertDialog.Builder(this)
                .setTitle(provider)
                .setMessage(message)
                .setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        sendConfirmation(orderId, "1");
                    }
                })
                .setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        sendConfirmation(orderId, "2");
                    }
                })
                .show();
    }

    private void sendConfirmation(String orderId, String confirmed, String note) {
        Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/confirmOrder.aspx")
                .appendQueryParameter("orderId", orderId)
                .appendQueryParameter("confirmed", confirmed)
                .appendQueryParameter("note", note)
                .build();
        new HttpRequest(this, uri, false)
                .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                    @Override
                    public void onHttpResult(String result) {
                        adapter.refreshCalendar();
                    }
                });
    }

    /**
     * @param month
     * @param year
     */
    private void setGridCellAdapterToDate(int month, int year) {
        adapter.setCalendarDate(year, month, 1);
//        adapter = new GridCellAdapter(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
        updateCalendar(month, year);
    }

    private void updateCalendar(int month, int year) {
        _calendar.set(year, month, _calendar.get(Calendar.DAY_OF_MONTH));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
            Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else {
                month++;
            }
            Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: " + month + " Year: " + year);
            setGridCellAdapterToDate(month, year);
        }

    }

    @Override
    public void onDestroy() {
        Log.d(tag, "Destroying View ...");
        super.onDestroy();
    }

    // ///////////////////////////////////////////////////////////////////////////////////////
    // Inner Class
    public class GridCellAdapter extends BaseAdapter implements OnClickListener {
        private static final String tag = "GridCellAdapter";
        private final Context _context;

        private List<String> list;
        private static final int DAY_OFFSET = 2;
        private final String[] weekdays = new String[]{"Ned", "Pon", "Uto", "Sri", "Čet", "Pet", "Sub"};
        private final String[] months = {"Siječanj", "Veljača", "Ožujak", "Travanj", "Svibanj", "Lipanj", "Srpanj", "Kolovoz", "Rujan", "Listopad", "Studeni", "Prosinac"};
        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private int month, year;
        private int daysInMonth, prevMonthDays;
        private Calendar currentDate;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private Button gridcell;
        private TextView num_events_per_day;

        public HashMap<String, ArrayList<Order>> monthOrders = new HashMap<String, ArrayList<Order>>();
        private String selectedDate;
        private int selectedOrder = -1;

        private Runnable updateOrders = new Runnable() {
            @Override
            public void run() {
                refreshCalendar();
                updater.postDelayed(this, 10000);
            }
        };
        private Handler updater = new Handler();

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId, int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            this.month = month;
            this.year = year;

            Log.d(tag, "==> Passed in Date FOR Month: " + month + " " + "Year: " + year);
            Calendar calendar = Calendar.getInstance();
            currentDate = new GregorianCalendar();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
            Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
            Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
            Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

            // Print Month
            currentMonth.setText(getMonthAsString(month - 1) + ", " + year + ".");
            currentDay.setText(new DateFormat().format("d.M.yyyy.", currentDate).toString());
            selectedDate = new DateFormat().format("d-M-yyyy", currentDate).toString();

            printMonth(month, year);

//            updater.post(updateOrders);
        }

        public void setCalendarDate(int year, int month, int day) {
            this.list = new ArrayList<String>();
            this.month = month;
            this.year = year;

            currentMonth.setText(getMonthAsString(month - 1) + ", " + year + ".");
            printMonth(month, year);
            refreshCalendar();
        }

        public void refreshCalendar() {
            final SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
            String userId = prefs.getString("id", "");
            Uri uri = new Uri.Builder().encodedPath("http://nas2skupa.com/5do12/getProOrders.aspx")
                    .appendQueryParameter("proId", userId)
                    .appendQueryParameter("year", String.valueOf(year))
                    .appendQueryParameter("month", String.valueOf(month))
                    .build();
            new HttpRequest(getApplicationContext(), uri, true)
                    .setOnHttpResultListener(new HttpRequest.OnHttpResultListener() {
                        @Override
                        public void onHttpResult(String result) {
                            parseServerResult(result);
                        }
                    });
        }

        private void parseServerResult(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONArray jsonArray = jsonObj.getJSONArray("ordersPro");
                HashMap<String, ArrayList<Order>> newMonthOrders = new HashMap<String, ArrayList<Order>>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    Order order = new Order(jsonArray.getJSONObject(i));
                    if (order.providerConfirm > 1 || order.userConfirm > 1) continue;
                    if (order.providerConfirm == 0) order.color = "#C90C62";
                    order.color = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})").matcher(order.color).matches() ? order.color : "#adbb02";
                    String key = new DateFormat().format("d-M-yyyy", order.date).toString();
                    if (newMonthOrders.containsKey(key))
                        newMonthOrders.get(key).add(order);
                    else
                        newMonthOrders.put(key, new ArrayList<Order>(Arrays.asList(order)));
                }

                if (monthOrders.equals(newMonthOrders)) return;
                monthOrders = newMonthOrders;

                this.notifyDataSetChanged();
                calendarView.setAdapter(this);
                drawDayOrders(monthOrders.get(selectedDate), 0);
            } catch (JSONException e) {
                Log.e("ActionHttpRequest", "Error parsing server data.");
                e.printStackTrace();
            }
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Prints Month
         *
         * @param mm
         * @param yy
         */
        private void printMonth(int mm, int yy) {
            Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
            // The number of days to leave blank at
            // the start of this month.
            int trailingSpaces = 0;
            int leadSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);

            Log.d(tag, "Current Month: " + " " + currentMonthName + " having " + daysInMonth + " days.");

            // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
            Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
                Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
                Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:" + prevMonth + " NextMonth: " + nextMonth + " NextYear: " + nextYear);
            }

            // Compute how much to leave before before the first day of the
            // month.
            // getDay() returns 0 for Sunday.
            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            Log.d(tag, "Week Day:" + currentWeekDay + " is " + getWeekDayAsString(currentWeekDay));
            Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
            Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

            if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
                ++daysInMonth;
            }

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                Log.d(tag, "PREV MONTH:= " + prevMonth + " => " + getMonthAsString(prevMonth) + " " + String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i));
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-PASSIVE" + "-" + (prevMonth + 1) + "-" + prevYear);
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                cal.set(Calendar.DAY_OF_MONTH, i);
                Log.d(currentMonthName, String.valueOf(i) + " " + getMonthAsString(currentMonth) + " " + yy);
                if (cal.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
                        && cal.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
                        && cal.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)) {
                    list.add(String.valueOf(i) + "-CURRENT" + "-" + (currentMonth + 1) + "-" + yy);
                } else {
                    list.add(String.valueOf(i) + "-ACTIVE" + "-" + (currentMonth + 1) + "-" + yy);
                }
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
                list.add(String.valueOf(i + 1) + "-PASSIVE" + "-" + (nextMonth + 1) + "-" + nextYear);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.calendar_day_gridcell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            gridcell.setOnClickListener(this);

            // ACCOUNT FOR SPACING

            Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
            String[] day_color = list.get(position).split("-");
            final String theday = day_color[0];
            final String color = day_color[1];
            final String themonth = day_color[2];
            final String theyear = day_color[3];
            final String key = theday + "-" + themonth + "-" + theyear;
            if (monthOrders.size() > 0) {
                if (monthOrders.containsKey(key)) {
                    try {
                        Calendar orderDate = Calendar.getInstance();
                        orderDate.setTime(new SimpleDateFormat("d-M-yyyy").parse(key));
                        num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
                        Integer numOrders = monthOrders.get(key).size();
                        num_events_per_day.setText(numOrders.toString());
                        for (Order o : monthOrders.get(key))
                            numOrders -= o.providerConfirm;
                        if (numOrders > 0)
                            num_events_per_day.setTextAppearance(_context, R.style.calendar_new_events_style);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Set the Day GridCell
            gridcell.setText(theday);
            gridcell.setTag(theday + "-" + themonth + "-" + theyear);
            Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-" + theyear);

            if (color.equals("PASSIVE")) {
                gridcell.setTextAppearance(_context, R.style.passive_month_day);
                gridcell.setClickable(false);
            }
            if (color.equals("ACTIVE")) {
                gridcell.setTextAppearance(_context, R.style.active_month_day);
            }
            if (color.equals("CURRENT")) {
                gridcell.setTextAppearance(_context, R.style.current_day);
            }
            return row;
        }

        @Override
        public void onClick(View view) {
            selectedDate = (String) view.getTag();
            selectedOrder = -1;
            String dateString = selectedDate.replace('-', '.').concat(".");
            currentDay.setText(dateString);
            ordersDetails.setText("");
            orderConfirmation.setVisibility(View.GONE);
            drawDayOrders(monthOrders.get(selectedDate), 0);
        }

        private void drawDayOrders(ArrayList<Order> input_orders, int row) {
            if (row == 0) ordersLayout.removeAllViews();
            if (input_orders == null) return;

            final int offset = 360;
            final float scale = (float) (ordersLayout.getMeasuredWidth() / 1020.0);
            int last = 0;
            ArrayList<Order> orders = new ArrayList<Order>(input_orders);
            ArrayList<Order> leftovers = new ArrayList<Order>();

            Collections.sort(orders, new Order.OrderTimeComparator());
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);

                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(order.startTime);
                int start = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                calendar.setTime(order.endTime);
                int end = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

                if (start < last) {
                    leftovers.add(order);
                    continue;
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins((int) ((start - offset) * scale), 35 * row + 5, 0, 5);
                Button orderButton = new Button(_context);
                orderButton.setTag(order);
                orderButton.setId(Integer.parseInt(order.id));
                orderButton.setText(order.serviceName);
                orderButton.setTextAppearance(_context, R.style.daily_event_style);
                orderButton.setMinimumWidth(0);
                orderButton.setMinimumHeight(0);
                orderButton.setMaxLines(1);
                orderButton.setPadding(5, 5, 5, 5);
                orderButton.setHeight(30);
                orderButton.setWidth((int) ((end - start) * scale));
                orderButton.setBackgroundColor(Color.parseColor(order.color));
                orderButton.setLayoutParams(params);
                orderButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedOrder = v.getId();
                        v.requestFocusFromTouch();
                        Order order = (Order) v.getTag();
                        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
                        ordersDetails.setText(order.uName + " " + order.uSurname + "\n"
                                + order.serviceName + ", " + tf.format(order.startTime) + "-" + tf.format(order.endTime) + "\n"
                                + order.servicePrice + " kn");
                        orderConfirmation.setTag(order);
                        orderConfirmation.setVisibility(order.providerConfirm == 0 ? View.VISIBLE : View.GONE);
                    }
                });
                orderButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus)
                            v.setBackgroundColor(Color.parseColor("#330F82"));
                        else {
                            Order order = (Order) v.getTag();
                            v.setBackgroundColor(Color.parseColor(order.color));
//                            ordersDetails.setText("");
                        }
                    }
                });
                ordersLayout.addView(orderButton);
                last = end;
            }
            if (leftovers.size() > 0)
                drawDayOrders(leftovers, ++row);
        }

        public void selectOrder(Order order) {
//            findViewById()
//            String key = new DateFormat().format("d-M-yyyy", order.date).toString();
//            if (selectedDate != key)

        }

        public void showOrdersDialog(String date, final ArrayList<Order> orders) {
            final int count = orders.size();
            final boolean[] selectedItems = new boolean[count];
            final String[] ordersDescriptions = new String[count];
            Calendar orderDate = Calendar.getInstance();
            try {
                orderDate.setTime(new SimpleDateFormat("dd.MM.yyyy.").parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
            for (int i = 0; i < count; i++) {
                Order order = orders.get(i);
                ordersDescriptions[i] = String.format("%s %s\n%s - %s\n%s (%s kn)",
                        order.uName,
                        order.uSurname,
                        tf.format(order.startTime),
                        tf.format(order.endTime),
                        order.serviceName,
                        order.servicePrice);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(Organizer.this);
            builder.setTitle(date);
            if (orderDate.before(currentDate))
                builder.setItems(ordersDescriptions, null);
            else {
                builder.setMultiChoiceItems(ordersDescriptions, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                selectedItems[which] = isChecked;
                            }
                        });
                builder.setPositiveButton("Otkaži označene", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        for (int i = 0; i < count; i++) {
//                            if (selectedItems[i])
//                                sendConfirmation(orders.get(i).id, "3");
//                        }
                    }
                });
            }
            builder.setNegativeButton("Zatvori", null);
            builder.show();
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }
}

