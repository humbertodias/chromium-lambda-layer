FROM public.ecr.aws/lambda/java:21 AS runtime

#COPY --from=build /app/build/libs ${LAMBDA_TASK_ROOT}
#COPY --from=build /app/target/dependency/* ${LAMBDA_TASK_ROOT}/lib/

COPY app/build/libs ${LAMBDA_TASK_ROOT}
#COPY layer/*.tar.gz /opt
COPY *.tar.gz /opt


CMD [ "com.example.App::handleRequest" ]

ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"

# gradle build
# docker build --platform linux/amd64 -t chromium-lambda:test .
# docker run -p 9000:8080 -p 5005:5005 chromium-lambda:test
# curl "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{}'
