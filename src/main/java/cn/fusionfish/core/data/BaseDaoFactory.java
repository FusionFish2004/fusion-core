package cn.fusionfish.core.data;

/**
 * @author JeremyHu
 */
public interface BaseDaoFactory<T extends BaseDao<?>> {
    /**
     * 生产新的实例
     * @return 实例
     */
    T newInstance();

}
