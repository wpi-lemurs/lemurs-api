import { Button } from "react-bootstrap";
import LemursAPK from "../downloadables/lemurs.apk";

export default function HomePage() {
    return (
        <div className="App">
            <h2 className="card-title">LEMURS Home Page</h2>
            <br />

            <Button
                href={LemursAPK}
                download="lemurs.apk"
                target="_blank"
                rel="noreferrer"
            >
                Download LEMURS APK for Android
            </Button>
        </div>
    );
}