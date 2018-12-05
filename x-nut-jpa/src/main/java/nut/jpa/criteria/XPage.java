package nut.jpa.criteria;

import com.querydsl.core.types.Order;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@Accessors(chain = true)
public class XPage<T> {

    public XPage() {
    }

    public XPage(int pageNumber, int pageSize,int totalPages) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        if (pageNumber+1 < totalPages) {
            this.hasNext = true;
        }
    }

    public XPage(int pageNumber, int pageSize,int totalPages, List content) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.content = content;
        this.totalPages = totalPages;
        if (pageNumber+1 < totalPages) {
            this.hasNext = true;
        }
    }


    public XPage(int pageNumber, int pageSize,int totalPages, int total, List content) {
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.content = content;
        this.total = total;
        if (pageNumber+1 < totalPages) {
            this.hasNext = true;
        }
    }

    private int pageNumber;
    private int totalPages;
    private int pageSize;
    private int total;
    private List<T> content;
    private List<XPageOrder> orders;
    private boolean hasNext = false;

    public static XPage build(Page page) {
        XPage xPage = new XPage();
        xPage.setPageNumber(page.getNumber());
        xPage.setPageSize(page.getSize());
        xPage.setTotal(Math.toIntExact(page.getTotalElements()));
        xPage.setTotalPages(page.getTotalPages());
        xPage.setContent(page.getContent());
        if(page.getSort() != null){
            List<XPageOrder> orders = new ArrayList<>();
            Iterator<Sort.Order> sos= page.getSort().iterator();
            while (sos.hasNext()){
                Sort.Order order = sos.next();
                orders.add(new XPageOrder(order.getProperty(),order.getDirection().name()));
            }
            xPage.setOrders(orders);
        }

        if (xPage.getPageNumber()+1 < xPage.getTotalPages()) {
            xPage.setHasNext(true);
        }
        return xPage;
    }

}
