FROM public.ecr.aws/lambda/java:11

COPY app/build/libs ${LAMBDA_TASK_ROOT}
COPY layer /opt

CMD [ "com.example.App::handleRequest" ]
