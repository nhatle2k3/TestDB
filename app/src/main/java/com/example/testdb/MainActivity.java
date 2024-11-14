package com.example.testdb;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editTenKS, editSLphongtrong, editGiaTien, editTrangthai;
    Button btnuploadAnh, btnThem, btnSua, btnXoa, btnTruyVan;
    ImageView imgUpload;
    ListView lvDanhsach;
    ArrayList<String> mylist;
    ArrayAdapter<String> myadapter;
    private List<String> list;
    private Spinner spinner;
    private static final int PICK_IMAGE_REQUEST = 1;
    SQLiteDatabase dataPhong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTenKS = (EditText) findViewById(R.id.editTenKS);
        editSLphongtrong = (EditText) findViewById(R.id.editSLphongtrong);
        editGiaTien = (EditText) findViewById(R.id.editGiaTien);
        editTrangthai = (EditText) findViewById(R.id.editTrangthai);

        btnuploadAnh = (Button) findViewById(R.id.btnuploadAnh);
        btnThem = (Button) findViewById(R.id.btnThem);
        btnSua = (Button) findViewById(R.id.btnSua);
        btnXoa = (Button) findViewById(R.id.btnXoa);
        btnTruyVan = (Button) findViewById(R.id.btnTruyVan);

        imgUpload = (ImageView) findViewById(R.id.imgUpload);

        lvDanhsach = (ListView) findViewById(R.id.lvDanhsach);

        spinner =(Spinner) findViewById(R.id.spntrangthai);
        list = new ArrayList<>();
        list.add("Rảnh");
        list.add("Bận");

        ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editTrangthai.setText(list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnuploadAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });

        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist);
        lvDanhsach.setAdapter(myadapter);
        dataPhong =openOrCreateDatabase("QlyPhong.db", MODE_PRIVATE, null);
        try{
            String sql = "CREATE TABLE tblphong(tenphong TEXT, SoLuong INTEGER, GiaTien REAL, Anh BLOB, TrangThai TEXT)";
            dataPhong.execSQL(sql);
        }catch (Exception e){
            Log.e("Error", "Table đã tồn tại");
        }
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenPhong = editTenKS.getText().toString().trim();
                int soLuong = Integer.parseInt(editSLphongtrong.getText().toString().trim());
                double giaTien = Double.parseDouble(editGiaTien.getText().toString().trim());
                byte[] anh = imageViewToByte(imgUpload);
                String trangThai = spinner.getSelectedItem().toString();
                ContentValues myvalue = new ContentValues();
                myvalue.put("tenphong", tenPhong);
                myvalue.put("SoLuong", soLuong);
                myvalue.put("GiaTien", giaTien);
                myvalue.put("Anh", anh);
                myvalue.put("TrangThai", trangThai);
                String msg = "";
                if (dataPhong.insert("tblphong", null, myvalue) == -1) {
                    msg = "Thêm thông tin thất bại";
                } else {
                    msg = "Thêm thông tin thành công";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                editTenKS.setText("");
                editSLphongtrong.setText("");
                editGiaTien.setText("");
                editTrangthai.setText("");
                spinner.setSelection(0);
                editTenKS.requestFocus();
                imgUpload.setImageResource(0);
            }
        });

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenPhong = editTenKS.getText().toString().trim();
                int soLuong = Integer.parseInt(editSLphongtrong.getText().toString().trim());
                double giaTien = Double.parseDouble(editGiaTien.getText().toString().trim());
                byte[] anh = imageViewToByte(imgUpload);
                String trangThai = spinner.getSelectedItem().toString();

                String updateSQL = "UPDATE tblphong SET SoLuong = ?, GiaTien = ?, Anh = ?, TrangThai = ? WHERE Tenphong = ?";
                SQLiteStatement statement = dataPhong.compileStatement(updateSQL);

                statement.bindLong(1, soLuong);
                statement.bindDouble(2, giaTien);
                statement.bindBlob(3, anh);
                statement.bindString(4, trangThai);
                statement.bindString(5, tenPhong);

                int rowsAffected = statement.executeUpdateDelete();

                if (rowsAffected > 0) {
                    Toast.makeText(MainActivity.this, "Cập nhật phòng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy phòng để sửa!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenPhong = editTenKS.getText().toString().trim();

                if (tenPhong.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên phòng cần xóa!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String deleteSQL = "DELETE FROM tblphong WHERE Tenphong = ?";
                SQLiteStatement statement = dataPhong.compileStatement(deleteSQL);

                statement.bindString(1, tenPhong);

                int rowsAffected = statement.executeUpdateDelete();

                if (rowsAffected > 0) {
                    Toast.makeText(MainActivity.this, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy phòng để xóa!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnTruyVan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mylist.clear();
                Cursor c = dataPhong.query("tblphong", null, null, null, null, null, null);
                if (c != null) {
                    while (c.moveToNext()) {
                        String data = "Tên phòng: " + c.getString(0) + "\n" +
                                "Số lượng: " + c.getInt(1) + "\n" +
                                "Giá tiền: " + c.getDouble(2) + "\n" +
                                "Trạng thái: " + c.getString(4);
                        mylist.add(data);
                    }
                    c.close();
                }
                myadapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Truy vấn dữ liệu thành công!", Toast.LENGTH_SHORT).show();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Chuyển URI của ảnh đã chọn thành Bitmap và hiển thị trên ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgUpload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}