@echo off
setlocal

REM 获取当前日期
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set "datetime=%%I"
set "year=%datetime:~0,4%"
set "month=%datetime:~4,2%"
set "day=%datetime:~6,2%"

git init
git add .
git commit -m "Update on %year%-%month%-%day%"
REM 关联远程仓库
git remote remove origin
git remote add origin github.com/xk-12/xk_3d_viewer_test.git

REM 提交更改

REM 推送更改到GitHub
git push -f origin main

endlocal