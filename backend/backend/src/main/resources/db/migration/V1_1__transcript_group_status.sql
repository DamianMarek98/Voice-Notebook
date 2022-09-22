alter table transcript_group
add column transcribe_status varchar(255);

update transcript_group
set transcribe_status = 'FINISHED';
