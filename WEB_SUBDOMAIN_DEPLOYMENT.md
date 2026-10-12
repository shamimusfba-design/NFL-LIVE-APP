# NFL LIVE Web App — subdomain deployment

This deployment package is the existing React/Vite frontend compiled as a static website. It is intended for a demo/preview with synthetic data only.

## Build a ready-to-upload ZIP

1. Open GitHub Actions for this repository.
2. Run **Build NFL LIVE Web App** on branch `deploy/web-subdomain-demo`, or wait for the workflow after pushing frontend changes.
3. Open the successful workflow run.
4. Download the `nfl-live-web-upload` artifact.
5. Extract the ZIP on your computer. The ZIP contains the website files, including `index.html`, assets, and `.htaccess`.

## Upload to a subdomain

1. Create a subdomain in your hosting panel, for example `preview.nflbd.com`.
2. Set its document root to a dedicated empty folder, for example `public_html/preview`. Do not overwrite the main domain's website.
3. Upload the extracted ZIP to that document root and extract it there.
4. Confirm `index.html` is directly inside the subdomain's document root, not inside an extra nested folder.
5. Enable HTTPS for the subdomain in your hosting panel.
6. Open the HTTPS subdomain in a private/incognito browser window and test login, mobile layout, TV/Desktop view, demo dashboard, entry forms, incidents and reports.

## Important scope and limitations

- This is a frontend-only demo build. It uses the existing app's in-memory state and generated synthetic demo records.
- Refreshing the page resets session state/demo records. Different devices do not share data.
- Do not enter real factory data or real passwords. The current UI's sample-user login and role switching are not secure production authentication.
- No backend, database, or Google credentials are needed to host this static demo.
- A later installable Android app can be created from the same React/Vite frontend using the repository's existing Capacitor setup. The website should first be validated in the browser.
- This workflow creates a ZIP artifact; it does not upload to your hosting account automatically.
