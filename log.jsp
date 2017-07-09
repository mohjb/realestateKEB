<%!/*
create table log(
    entity  varchar(255),
    col     varchar(255),
    id      varchar(255),
    val     text,
    logTime timestamp,
    lm      timestamp,
    usr     varchar(255),


    index(logTime ,entity ,id),
    index(usr     ,entity ,lm),
    index(entity  ,col    ,logTime),
    index(entity  ,id     ,logTime),
    -- unique(entity,col  ,id)
);

get log after param lm

get all unique-cols by params entity+id ,by most-recent logTime

get all unique entity+col

get all unique entity+id  , 

*/%>
