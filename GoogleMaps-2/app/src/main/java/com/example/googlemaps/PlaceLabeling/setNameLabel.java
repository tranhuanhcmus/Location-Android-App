package com.example.googlemaps.PlaceLabeling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.googlemaps.R;

public class setNameLabel extends AppCompatActivity {

    private EditText editText;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name_label);

        getSupportActionBar().hide();

        editText = findViewById(R.id.edit_text);

        Search(editText);
        db = this.openOrCreateDatabase("myDB",MODE_PRIVATE,null);

    }

    public void Search(EditText editText){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // kiểm tra nếu người dùng bấm nút search ở bàn phím
                // Hoặc bấm nút done
                // Hoặc bấm nút enter
                // Hoặc chạm
                if(i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){


                    if(!editText.getText().toString().equals("")){
                        // lấy địa chỉ từ activity chọn địa điểm
                        Bundle previousData = getIntent().getExtras();
                        String address = previousData.getString("Address");
                        String name = editText.getText().toString();

                        //Thêm nhãn vào sqlite
                        String query = "insert into Nhan(name, address) values (?,?)";
                        db.execSQL(query,new Object[]{name, address});


                        // trả về activity trước

                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }



                    return true;
                }
                return false;
            }
        });
    }

    public void backActivity(View view){
        onBackPressed();
    }
}