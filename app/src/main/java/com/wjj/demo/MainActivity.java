package com.wjj.demo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.example.Person;
import com.example.PersonDao;
import com.wjj.demo.adapter.PersonAdapter;
import com.wjj.demo.application.MyApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.Query;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.delete_id_tv)
    EditText deleteIdTv;
    private List<Person> list;
    private Cursor cursor;
    private PersonAdapter adapter;

    @BindView(R.id.add)
    Button add;
    @BindView(R.id.delete)
    Button delete;
    @BindView(R.id.edit)
    Button edit;
    @BindView(R.id.query)
    Button query;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.input_address)
    EditText inputAddress;
    @BindView(R.id.input_age)
    EditText inputAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        cursor = getDb().query(getPersonDao().getTablename(), getPersonDao().getAllColumns(), null, null, null, null, null);
        list = new ArrayList<>();
        adapter = new PersonAdapter(MainActivity.this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //返回Dao对象
    private PersonDao getPersonDao() {
        return ((MyApplication) this.getApplicationContext()).getDaoSession().getPersonDao();
    }

    //返回数据库对象
    private SQLiteDatabase getDb() {
        // 通过 BaseApplication 类提供的 getDb() 获取具体 db
        return ((MyApplication) this.getApplicationContext()).getDb();
    }

    @OnClick({R.id.add, R.id.delete, R.id.edit, R.id.query})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                addPerson();
                break;
            case R.id.delete:
                String v = deleteIdTv.getText().toString().trim();
                if (v.length() > 0) {
                    deletePerson(Long.parseLong(v));
                } else {
                    Toast.makeText(getApplicationContext(), "删除所需的TextView值为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit:
                String age = inputAge.getText().toString().trim();
                editPerson(age);
                inputAge.setText("");
                break;
            case R.id.query:
                list.clear();
                queryPerson();
                break;
        }
    }

    //增
    private void addPerson() {
        LogUtils.d("---> addPerson ");
        String age = inputAge.getText().toString().trim();
        String name = inputName.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        //判断不为空
        if (address.length() > 0 && name.length() > 0 && age.length() > 0) {
            inputAddress.setText("");
            inputAge.setText("");
            inputName.setText("");

            Person person = new Person(null, name, age, address);
            getPersonDao().insert(person);
            cursor.requery();

        } else {
            Toast.makeText(getApplicationContext(), "有一个/多个输入框里没东西", Toast.LENGTH_SHORT).show();
        }
    }

    //查
    private void queryPerson() {
        LogUtils.d("---> queryPerson ");
        String age = inputAge.getText().toString().trim();
        String name = inputName.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        //判断是否有东西
        if (name.length() > 0) {
            LogUtils.d("---> name.length() > 0 ");
            inputAge.setText("");
            inputAddress.setText("");
            queryLogic(name, 1);

        } else if (age.length() > 0) {
            LogUtils.d("---> age.length() > 0 ");
            inputName.setText("");
            inputAddress.setText("");

            queryLogic(age, 3);

        } else if (address.length() > 0) {
            LogUtils.d("---> address.length() > 0 ");
            inputName.setText("");
            inputAge.setText("");

            queryLogic(address, 2);  // Query 类代表了一个可以被重复执行的查询

        } else {
            Toast.makeText(getApplicationContext(), "有一个/多个输入框里没东西", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryLogic(String value, int type) {
        Query query = null;
        switch (type) {
            case 1:
                // Query 类代表了一个可以被重复执行的查询
                query = getQuery(value, PersonDao.Properties.Name);
                break;
            case 2:
                query = getQuery(value, PersonDao.Properties.Address);
                break;
            case 3:
                query = getQuery(value, PersonDao.Properties.Age);
                break;
        }
        if (query != null) {
            // 查询结果以 List 返回
            List<Person> queryList = (List<Person>) query.list();
            list.addAll(queryList);
            Toast.makeText(getApplicationContext(), "有" + queryList.size() + "个值", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }

    }

    private Query getQuery(String value, Property p) {
        Query query = null;
        query = getPersonDao().queryBuilder()
                //逻辑
                .where(p.eq(value))
                //排序
                .orderAsc(PersonDao.Properties.Id)
                .build();
        return query;

    }

    //删
    private void deletePerson(long value) {
        LogUtils.d("---> deletePerson value = " + value);
        getPersonDao().deleteByKey(value);
        deleteIdTv.setText("");
    }

    //改
    private void editPerson(String age) {
        LogUtils.d("---> editPerson");
        Person person = new Person();
        person.setAddress("suzhou");
        person.setAge(age);
        person.setName("kobe");
        person.setId(2l);
        getPersonDao().update(person);
    }

}
