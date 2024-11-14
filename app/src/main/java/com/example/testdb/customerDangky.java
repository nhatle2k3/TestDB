package com.example.testdb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class customerDangky extends AppCompatActivity {
    EditText editTK, editMK, editreMK;
    Button btnlogin, btnsignin;
    ImageView imageView;
    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dangky);
        editTK = (EditText) findViewById(R.id.editTK);
        editMK = (EditText) findViewById(R.id.editMK);
        editreMK = (EditText) findViewById(R.id.editReMK);

        btnlogin = (Button) findViewById(R.id.btnLogin);
        btnsignin = (Button) findViewById(R.id.btnSingin);
        imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.ava);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTK.getText().toString().trim();
                String password = editMK.getText().toString().trim();
                String repass = editreMK.getText().toString().trim();

                if (!password.equals(repass)) {
                    Toast.makeText(customerDangky.this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lưu vào cơ sở dữ liệu nếu chưa tồn tại người dùng với tên đăng nhập đó
                boolean isInserted = dbHelper.insertUser(username, password);
                if (isInserted) {
                    Toast.makeText(customerDangky.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển về trang đăng nhập
                    Intent intent = new Intent(customerDangky.this, Customer .class);  // Thay LoginActivity bằng activity đăng nhập của bạn
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(customerDangky.this, "Tên đăng nhập đã tồn tại hoặc có lỗi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}