import com.me.seckill.dto.Exposer;
import com.me.seckill.dto.SeckillExcecution;
import com.me.seckill.entity.Seckill;
import com.me.seckill.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList(){
        List<Seckill> seckills = seckillService.getSeckillList();
        System.out.println(seckills);
    }

    @Test
    public void testGetById(){
        long id = 1000;
        Seckill seckill = seckillService.getById(1000);
        System.out.println(seckill);
    }

    @Test
    public void testSeckillLogic(){
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(!exposer.isExposed()){
            logger.warn("not open yet!");
        }else {
            logger.info("exposer={}",exposer);
            long phone = 1111111l;
            String md5 = exposer.getMd5();
            SeckillExcecution excecution = seckillService.excuteSeckill(id,phone,md5);
            logger.info("result:",excecution);
        }
    }

}
