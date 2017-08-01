package com.njjd.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.njjd.domain.QuestionEntity;
import com.njjd.domain.IndexNavEntity;

import com.njjd.dao.QuestionEntityDao;
import com.njjd.dao.IndexNavEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig questionEntityDaoConfig;
    private final DaoConfig indexNavEntityDaoConfig;

    private final QuestionEntityDao questionEntityDao;
    private final IndexNavEntityDao indexNavEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        questionEntityDaoConfig = daoConfigMap.get(QuestionEntityDao.class).clone();
        questionEntityDaoConfig.initIdentityScope(type);

        indexNavEntityDaoConfig = daoConfigMap.get(IndexNavEntityDao.class).clone();
        indexNavEntityDaoConfig.initIdentityScope(type);

        questionEntityDao = new QuestionEntityDao(questionEntityDaoConfig, this);
        indexNavEntityDao = new IndexNavEntityDao(indexNavEntityDaoConfig, this);

        registerDao(QuestionEntity.class, questionEntityDao);
        registerDao(IndexNavEntity.class, indexNavEntityDao);
    }
    
    public void clear() {
        questionEntityDaoConfig.clearIdentityScope();
        indexNavEntityDaoConfig.clearIdentityScope();
    }

    public QuestionEntityDao getQuestionEntityDao() {
        return questionEntityDao;
    }

    public IndexNavEntityDao getIndexNavEntityDao() {
        return indexNavEntityDao;
    }

}