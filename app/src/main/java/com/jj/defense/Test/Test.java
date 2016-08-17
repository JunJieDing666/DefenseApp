package com.jj.defense.Test;

import android.test.AndroidTestCase;

import com.jj.defense.DB.Domain.BlackNumberInfo;
import com.jj.defense.Engine.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/8/16.
 */
public class Test extends AndroidTestCase {
    public void testInsert() {
        BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
        //dao.insert("110", "1");
        //dao.insert("120", "1");
        for (int i = 0; i < 100; i++) {
            dao.insert("1860000000" + i, 1 + new Random().nextInt(3) + "");
        }
    }

    public void testDelete() {
        BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
        //dao.delete("120");
    }

    public void testUpdate() {
        BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
        //dao.update("120","2");
    }

    public void testQueryAll() {
        BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
        List<BlackNumberInfo> blackNumberInfos = dao.queryAll();
    }
}
