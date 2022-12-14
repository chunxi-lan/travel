package web.travel.dao;

import web.travel.domain.Route;

import java.util.List;

public interface RouteDao {

    /**
     * 根据cid查询总记录数
     * @param cid
     * @return
     */
    public int findTotalCount(int cid,String rname);

    /**
     * 根据cid start pageSize 查询当前页的数据（集合）
     * @param cid
     * @param start
     * @param pageSize
     * @param rname
     * @return
     */
    public List<Route> findByPage(int cid,int start,int pageSize,String rname);

    /**
     * 根据id查询一详情页面
     * @param rid
     * @return
     */
    public Route findOne(int rid);
}
