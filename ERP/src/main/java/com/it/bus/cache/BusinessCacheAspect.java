package com.it.bus.cache;

import com.it.bus.domain.Customer;

import com.it.bus.domain.Goods;
import com.it.bus.domain.Provider;
import com.it.sys.cache.CachePool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : Brave
 * @Version : 1.0
 * @Date : 2020/1/12 19:44
 */
@Aspect
@Component
@EnableAspectJAutoProxy
public class BusinessCacheAspect {

    /**
     * 日志出处
     */
    private Log log = LogFactory.getLog(BusinessCacheAspect.class);

    // 声明一个缓存容器
    private static Map<String, Object> CACHE_CONTAINER = CachePool.CACHE_CONTAINER;

    //注入redis模板
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    // 声明切面表达式
    private static final String POINTCUT_CUSTOMER_ADD = "execution(* com.it.bus.service.impl.CustomerServiceImpl.save(..))";
    private static final String POINTCUT_CUSTOMER_UPDATE = "execution(* com.it.bus.service.impl.CustomerServiceImpl.updateById(..))";
    private static final String POINTCUT_CUSTOMER_GET = "execution(* com.it.bus.service.impl.CustomerServiceImpl.getById(..))";
    private static final String POINTCUT_CUSTOMER_DELETE = "execution(* com.it.bus.service.impl.CustomerServiceImpl.removeById(..))";
    private static final String POINTCUT_CUSTOMER_BATCHDELETE = "execution(* com.it.bus.service.impl.CustomerServiceImpl.removeByIds(..))";

    private static final String CACHE_CUSTOMER_PROFIX = "customer:";

    public void initSerizler(){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    }

    /**
     * 客户添加切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_CUSTOMER_ADD)
    public Object cacheCustomerAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        initSerizler();
        // 取出第一个参数
        Customer object = (Customer) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            //redis做缓存
            ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
            opsForValue.set(CACHE_CUSTOMER_PROFIX + object.getId(), object);
            //CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + object.getId(), object);
        }
        return res;
    }

    /**
     * 查询切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_CUSTOMER_GET)
    public Object cacheCustomerGet(ProceedingJoinPoint joinPoint) throws Throwable {
        initSerizler();
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 从缓存里面取
        //Object res1 = CACHE_CONTAINER.get(CACHE_CUSTOMER_PROFIX + object);
        Object res1 = opsForValue.get(CACHE_CUSTOMER_PROFIX + object);
        if (res1 != null) {
            log.info("已从缓存里面找到客户对象" + CACHE_CUSTOMER_PROFIX + object);
            return res1;
        } else {
            //如果没有则查询数据库，并放入缓存
            Customer res2 = (Customer) joinPoint.proceed();
            opsForValue.set(CACHE_CUSTOMER_PROFIX + res2.getId(), res2);
            //CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + res2.getId(), res2);
            log.info("未从缓存里面找到客户对象，去数据库查询并放到缓存"+CACHE_CUSTOMER_PROFIX+res2.getId());
            return res2;
        }
    }

    /**
     * 更新切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_CUSTOMER_UPDATE)
    public Object cacheCustomerUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        initSerizler();
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        // 取出第一个参数
        Customer customerVo = (Customer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            //从缓存中取
            Customer customer = (Customer) opsForValue.get(CACHE_CUSTOMER_PROFIX + customerVo.getId());
            //Customer customer = (Customer) CACHE_CONTAINER.get(CACHE_CUSTOMER_PROFIX + customerVo.getId());
            if (null == customer) {
                customer = new Customer();
            }
            BeanUtils.copyProperties(customerVo, customer);
            log.info("客户对象缓存已更新" + CACHE_CUSTOMER_PROFIX + customerVo.getId());
            //CACHE_CONTAINER.put(CACHE_CUSTOMER_PROFIX + customer.getId(), customer);
            opsForValue.set(CACHE_CUSTOMER_PROFIX + customer.getId(), customer);
        }
        return isSuccess;
    }

    /**
     * 删除切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_CUSTOMER_DELETE)
    public Object cacheCustomerDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        initSerizler();
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            //CACHE_CONTAINER.remove(CACHE_CUSTOMER_PROFIX + id);
            redisTemplate.delete(CACHE_CUSTOMER_PROFIX + id);
            log.info("客户对象缓存已删除" + CACHE_CUSTOMER_PROFIX + id);
        }
        return isSuccess;
    }

    /**
     * 批量删除切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_CUSTOMER_BATCHDELETE)
    public Object cacheCustomerBatchDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        initSerizler();
        // 取出第一个参数
        @SuppressWarnings("unchecked")
        Collection<Serializable> idList = (Collection<Serializable>) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            for (Serializable id : idList) {
                // 删除缓存
                //CACHE_CONTAINER.remove(CACHE_CUSTOMER_PROFIX + id);
                redisTemplate.delete(CACHE_CUSTOMER_PROFIX + id);
                log.info("客户对象缓存已删除" + CACHE_CUSTOMER_PROFIX + id);
            }
        }
        return isSuccess;
    }

    /*// 声明切面表达式
    private static final String POINTCUT_PROVIDER_ADD = "execution(* com.it.bus.service.impl.ProviderServiceImpl.save(..))";
    private static final String POINTCUT_PROVIDER_UPDATE = "execution(* com.it.bus.service.impl.ProviderServiceImpl.updateById(..))";
    private static final String POINTCUT_PROVIDER_GET = "execution(* com.it.bus.service.impl.ProviderServiceImpl.getById(..))";
    private static final String POINTCUT_PROVIDER_DELETE = "execution(* com.it.bus.service.impl.ProviderServiceImpl.removeById(..))";
    private static final String POINTCUT_PROVIDER_BATCHDELETE = "execution(* com.it.bus.service.impl.ProviderServiceImpl.removeByIds(..))";

    private static final String CACHE_PROVIDER_PROFIX = "provider:";

    *//**
     * 供应商添加切入
     *
     * @throws Throwable
     *//*
    @Around(value = POINTCUT_PROVIDER_ADD)
    public Object cacheProviderAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Provider object = (Provider) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + object.getId(), object);
        }
        return res;
    }

    *//**
     * 查询切入
     *
     * @throws Throwable
     *//*
    @Around(value = POINTCUT_PROVIDER_GET)
    public Object cacheProviderGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_PROVIDER_PROFIX + object);
        if (res1 != null) {
            log.info("已从缓存里面找到供应商对象" + CACHE_PROVIDER_PROFIX + object);
            return res1;
        } else {
            Provider res2 = (Provider) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + res2.getId(), res2);
            log.info("未从缓存里面找到供应商对象，去数据库查询并放到缓存"+CACHE_PROVIDER_PROFIX+res2.getId());
            return res2;
        }
    }

    *//**
     * 更新切入
     *
     * @throws Throwable
     *//*
    @Around(value = POINTCUT_PROVIDER_UPDATE)
    public Object cacheProviderUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Provider providerVo = (Provider) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            Provider provider = (Provider) CACHE_CONTAINER.get(CACHE_PROVIDER_PROFIX + providerVo.getId());
            if (null == provider) {
                provider = new Provider();
            }
            BeanUtils.copyProperties(providerVo, provider);
            log.info("供应商对象缓存已更新" + CACHE_PROVIDER_PROFIX + providerVo.getId());
            CACHE_CONTAINER.put(CACHE_PROVIDER_PROFIX + provider.getId(), provider);
        }
        return isSuccess;
    }

    *//**
     * 删除切入
     *
     * @throws Throwable
     *//*
    @Around(value = POINTCUT_PROVIDER_DELETE)
    public Object cacheProviderDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            CACHE_CONTAINER.remove(CACHE_PROVIDER_PROFIX + id);
            log.info("供应商对象缓存已删除" + CACHE_PROVIDER_PROFIX + id);
        }
        return isSuccess;
    }

    *//**
     * 批量删除切入
     *
     * @throws Throwable
     *//*
    @Around(value = POINTCUT_PROVIDER_BATCHDELETE)
    public Object cacheProviderBatchDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        @SuppressWarnings("unchecked")
        Collection<Serializable> idList = (Collection<Serializable>) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            for (Serializable id : idList) {
                // 删除缓存
                CACHE_CONTAINER.remove(CACHE_PROVIDER_PROFIX + id);
                log.info("供应商对象缓存已删除" + CACHE_PROVIDER_PROFIX + id);
            }
        }
        return isSuccess;
    }*/

    //商品数据的缓存  声明切面表达式
    private static final String POINTCUT_GOODS_ADD = "execution(* com.it.bus.service.impl.GoodsServiceImpl.save(..))";
    private static final String POINTCUT_GOODS_UPDATE = "execution(* com.it.bus.service.impl.GoodsServiceImpl.updateById(..))";
    private static final String POINTCUT_GOODS_GET = "execution(* com.it.bus.service.impl.GoodsServiceImpl.getById(..))";
    private static final String POINTCUT_GOODS_DELETE = "execution(* com.it.bus.service.impl.GoodsServiceImpl.removeById(..))";

    private static final String CACHE_GOODS_PROFIX = "goods:";

    /**
     * 商品添加切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_ADD)
    public Object cacheGoodsAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Goods object = (Goods) joinPoint.getArgs()[0];
        Boolean res = (Boolean) joinPoint.proceed();
        if (res) {
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + object.getId(), object);
        }
        return res;
    }

    /**
     * 查询切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_GET)
    public Object cacheGoodsGet(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer object = (Integer) joinPoint.getArgs()[0];
        // 从缓存里面取
        Object res1 = CACHE_CONTAINER.get(CACHE_GOODS_PROFIX + object);
        if (res1 != null) {
            log.info("已从缓存里面找到商品对象" + CACHE_GOODS_PROFIX + object);
            return res1;
        } else {
            Goods res2 = (Goods) joinPoint.proceed();
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + res2.getId(), res2);
            log.info("未从缓存里面找到商品对象，去数据库查询并放到缓存"+CACHE_GOODS_PROFIX+res2.getId());
            return res2;
        }
    }

    /**
     * 更新切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_UPDATE)
    public Object cacheGoodsUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Goods goodsVo = (Goods) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            Goods goods = (Goods) CACHE_CONTAINER.get(CACHE_GOODS_PROFIX + goodsVo.getId());
            if (null == goods) {
                goods = new Goods();
            }
            BeanUtils.copyProperties(goodsVo, goods);
            log.info("商品对象缓存已更新" + CACHE_GOODS_PROFIX + goodsVo.getId());
            CACHE_CONTAINER.put(CACHE_GOODS_PROFIX + goods.getId(), goods);
        }
        return isSuccess;
    }

    /**
     * 删除切入
     *
     * @throws Throwable
     */
    @Around(value = POINTCUT_GOODS_DELETE)
    public Object cacheGoodsDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 取出第一个参数
        Integer id = (Integer) joinPoint.getArgs()[0];
        Boolean isSuccess = (Boolean) joinPoint.proceed();
        if (isSuccess) {
            // 删除缓存
            CACHE_CONTAINER.remove(CACHE_GOODS_PROFIX + id);
            log.info("商品对象缓存已删除" + CACHE_GOODS_PROFIX + id);
        }
        return isSuccess;
    }

}
