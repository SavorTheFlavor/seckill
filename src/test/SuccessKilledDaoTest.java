import com.me.seckill.dao.SeckillDao;
import com.me.seckill.dao.SuccessKilledDao;
import com.me.seckill.entity.Seckill;
import com.me.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 配置spring和Junit整合
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;
    @Test
    public void testQueryById() {
        long id = 1001;
        int insertCount = successKilledDao.insertSuccessKilled(id,18022222L);
        System.out.println("insertCount:"+insertCount);
    }

    //java没有保存形参记录：queryAll(int offset, int limit）--》(int arg0,int arg1)
    @Test
    public void testQueryAll() {
        long id = 1001;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,18022222L);
        System.out.println(successKilled.getSeckill().getName());
    }


}
