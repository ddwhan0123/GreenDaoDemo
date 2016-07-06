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
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
        //初始化游标
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
                    LogUtils.e( "---> 删除所需的TextView值为空");
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
        Observable.create(new Observable.OnSubscribe<Person>() {

            @Override
            public void call(Subscriber<? super Person> subscriber) {
                Person person = null;
                String age = inputAge.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                String address = inputAddress.getText().toString().trim();
                //判断不为空
                if (address.length() > 0 && name.length() > 0 && age.length() > 0) {
                    person = new Person(null, name, age, address);
                    getPersonDao().insert(person);
                    cursor.requery();

                } else {
                    LogUtils.d("---> 有一个/多个输入框里没东西");
                }
                if (person != null) {
                    subscriber.onNext(person);
                } else {
                    Observable.error(new NullPointerException("person为空"));
                }

                LogUtils.d("---> add 新线程" + Thread.currentThread().getId());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Person>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e(e);
            }

            @Override
            public void onNext(Person person) {
                inputAddress.setText("");
                inputAge.setText("");
                inputName.setText("");
            }
        });
    }

    //查
    private void queryPerson() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String age = inputAge.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                String address = inputAddress.getText().toString().trim();
                //判断是否有东西
                if (name.length() > 0) {
                    queryLogic(name, 1);
                    subscriber.onNext(name);
                } else if (age.length() > 0) {
                    queryLogic(age, 3);
                    subscriber.onNext(age);
                } else if (address.length() > 0) {
                    subscriber.onNext(address);
                    queryLogic(address, 2);  // Query 类代表了一个可以被重复执行的查询
                } else {
                    LogUtils.d("有一个/多个输入框里没东西");
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e(e);
            }

            @Override
            public void onNext(String s) {
                inputAge.setText("");
                inputAddress.setText("");
                inputName.setText("");
            }
        });
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
            LogUtils.d("--->有" + queryList.size() + "个值");
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
        Observable.just(value).subscribeOn(Schedulers.io()).map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                getPersonDao().deleteByKey(aLong);
                return aLong;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                deleteIdTv.setText("");
            }
        });


    }

    //改
    private void editPerson(String age) {
        Observable.just(age).subscribeOn(Schedulers.io()).map(new Func1<String, Person>() {
            @Override
            public Person call(String s) {
                Person person = new Person();
                person.setAddress("suzhou");
                person.setAge(s);
                person.setName("kobe");
                person.setId(3l);
                getPersonDao().update(person);
                return person;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Person>() {
            @Override
            public void call(Person person) {
                LogUtils.d(person);
            }
        });
    }

}
