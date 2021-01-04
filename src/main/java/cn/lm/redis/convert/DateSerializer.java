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
import java.util.Date;

import cn.lm.redis.RedisConstants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.time.FastDateFormat;


/**
 * <p>
 * 带时区的序列化
 * </p>
 *
 * @author qingsheng.chen 2018/9/4 星期二 19:01
 */
public class DateSerializer extends JsonSerializer<Date> {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(RedisConstants.DATE_TIME_FORMAT);

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(DATE_FORMAT.format(date));
    }
}
