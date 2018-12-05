package nut.jpa.criteria;

import com.querydsl.core.types.dsl.EntityPathBase;

public interface ICriteria {

    EntityPathBase getQEntity();


    default int getPageSize(){
        return 15;
    }
    default int getPageNumber(){
        return 0;
    }

}
