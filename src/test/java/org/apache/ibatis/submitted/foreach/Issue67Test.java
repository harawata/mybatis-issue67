/*
 *    Copyright 2009-2013 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.foreach;

import static org.junit.Assert.*;

import java.io.Reader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Issue67Test {

  /**
   * This test does not use Spring at all.
   */
  @Test
  public void testWithoutSpring() throws Exception {
    SqlSessionFactory sqlSessionFactory = pureMyBatisSetup();
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      execQuery(mapper);
    } finally {
      sqlSession.close();
    }
  }

  private SqlSessionFactory pureMyBatisSetup() throws Exception {
    // create a SqlSessionFactory
    Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/foreach/mybatis-config.xml");
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    reader.close();

    // populate in-memory database
    SqlSession session = sqlSessionFactory.openSession();
    Connection conn = session.getConnection();
    reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/foreach/CreateDB.sql");
    ScriptRunner runner = new ScriptRunner(conn);
    runner.setLogWriter(null);
    runner.runScript(reader);
    reader.close();
    session.close();

    return sqlSessionFactory;
  }

  /**
   * This test uses Spring.
   */
  @Test
  public void testWithSpring() {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:org/apache/ibatis/submitted/foreach/applicationContext.xml");

    Mapper mapper = (Mapper) context.getBean("mapper");
    assertNotNull(mapper);
    execQuery(mapper);

    context.close();
  }

  private void execQuery(Mapper mapper) {
    ComboVO comboVo1 = new ComboVO();
    comboVo1.setCod(1);
    comboVo1.setCodStr("ID 1");
    ComboVO comboVo5 = new ComboVO();
    comboVo5.setCod(5);
    comboVo5.setCodStr("ID 5");
    List<ComboVO> list = new ArrayList<ComboVO>();
    list.add(comboVo1);
    list.add(comboVo5);
    List<TraduccionVO> results = mapper.getPoblacionesIn(list);
    Assert.assertEquals(2, results.size());
  }
}
