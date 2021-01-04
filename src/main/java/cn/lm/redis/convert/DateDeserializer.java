/*
 * Copyright 2020-present, HZERO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.lm.redis.convert;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import cn.lm.redis.RedisConstants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * 带时区的反序列化
 * </p>
 *
 * @author qingsheng.chen 2018/9/4 星期二 19:01
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    private static final Logger logger = LoggerFactory.getLogger(DateDeserializer.class);

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(RedisConstants.DATE_TIME_FORMAT);

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        try {
            return DATE_FORMAT.parse(jsonParser.getValueAsString());
        } catch (ParseException e) {
            logger.warn("date format error : {}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
