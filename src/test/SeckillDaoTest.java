
import com.me.seckill.dao.SeckillDao;
import com.me.seckill.entity.Seckill;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.*;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 配置spring和Junit整合
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;
//被idea那些mark as resource root之类的快搞疯了
    @Test
    public void testQueryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill.getCreateTime());
    }

    //java没有保存形参记录：queryAll(int offset, int limit）--》(int arg0,int arg1)
    @Test
    public void testQueryAll() {
        List<Seckill> seckills = seckillDao.queryAll(0,100);
        for (Seckill s:seckills) {
            System.out.println(s.getName());
        }
    }

    @Test
    public void testReduceNumber() {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L,killTime);
        System.out.println("update rows:"+updateCount);
    }

}
