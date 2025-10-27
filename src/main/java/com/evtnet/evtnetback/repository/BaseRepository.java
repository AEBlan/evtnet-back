package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository <E extends Base,ID extends Serializable> extends JpaRepository <E,ID>, JpaSpecificationExecutor<E> {
    
}
