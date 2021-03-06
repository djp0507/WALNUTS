package com.njjd.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.njjd.domain.BannerEntity;
import com.njjd.domain.IndexNavEntity;
import com.njjd.domain.QuestionEntity;
import com.njjd.domain.SearchHistory;
import com.njjd.domain.TagEntity;

import com.njjd.dao.BannerEntityDao;
import com.njjd.dao.IndexNavEntityDao;
import com.njjd.dao.QuestionEntityDao;
import com.njjd.dao.SearchHistoryDao;
import com.njjd.dao.TagEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bannerEntityDaoConfig;
    private final DaoConfig indexNavEntityDaoConfig;
    private final DaoConfig questionEntityDaoConfig;
    private final DaoConfig searchHistoryDaoConfig;
    private final DaoConfig tagEntityDaoConfig;

    private final BannerEntityDao bannerEntityDao;
    private final IndexNavEntityDao indexNavEntityDao;
    private final QuestionEntityDao questionEntityDao;
    private final SearchHistoryDao searchHistoryDao;
    private final TagEntityDao tagEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bannerEntityDaoConfig = daoConfigMap.get(BannerEntityDao.class).clone();
        bannerEntityDaoConfig.initIdentityScope(type);

        indexNavEntityDaoConfig = daoConfigMap.get(IndexNavEntityDao.class).clone();
        indexNavEntityDaoConfig.initIdentityScope(type);

        questionEntityDaoConfig = daoConfigMap.get(QuestionEntityDao.class).clone();
        questionEntityDaoConfig.initIdentityScope(type);

        searchHistoryDaoConfig = daoConfigMap.get(SearchHistoryDao.class).clone();
        searchHistoryDaoConfig.initIdentityScope(type);

        tagEntityDaoConfig = daoConfigMap.get(TagEntityDao.class).clone();
        tagEntityDaoConfig.initIdentityScope(type);

        bannerEntityDao = new BannerEntityDao(bannerEntityDaoConfig, this);
        indexNavEntityDao = new IndexNavEntityDao(indexNavEntityDaoConfig, this);
        questionEntityDao = new QuestionEntityDao(questionEntityDaoConfig, this);
        searchHistoryDao = new SearchHistoryDao(searchHistoryDaoConfig, this);
        tagEntityDao = new TagEntityDao(tagEntityDaoConfig, this);

        registerDao(BannerEntity.class, bannerEntityDao);
        registerDao(IndexNavEntity.class, indexNavEntityDao);
        registerDao(QuestionEntity.class, questionEntityDao);
        registerDao(SearchHistory.class, searchHistoryDao);
        registerDao(TagEntity.class, tagEntityDao);
    }
    
    public void clear() {
        bannerEntityDaoConfig.clearIdentityScope();
        indexNavEntityDaoConfig.clearIdentityScope();
        questionEntityDaoConfig.clearIdentityScope();
        searchHistoryDaoConfig.clearIdentityScope();
        tagEntityDaoConfig.clearIdentityScope();
    }

    public BannerEntityDao getBannerEntityDao() {
        return bannerEntityDao;
    }

    public IndexNavEntityDao getIndexNavEntityDao() {
        return indexNavEntityDao;
    }

    public QuestionEntityDao getQuestionEntityDao() {
        return questionEntityDao;
    }

    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

    public TagEntityDao getTagEntityDao() {
        return tagEntityDao;
    }

}
